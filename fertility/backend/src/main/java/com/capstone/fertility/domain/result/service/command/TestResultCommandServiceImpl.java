package com.capstone.fertility.domain.result.service.command;

import com.capstone.fertility.domain.result.entity.TestResult;
import com.capstone.fertility.domain.result.enums.RiskLevel;
import com.capstone.fertility.domain.result.repository.TestResultRepository;
import com.capstone.fertility.global.ai.client.AiPredictionClient;
import com.capstone.fertility.global.ai.client.dto.req.AiPredictionReqDTO;
import com.capstone.fertility.global.ai.mapping.AiLifestyleCategoryMapper;
import com.capstone.fertility.global.ai.client.dto.res.AiPredictionResDTO;
import com.capstone.fertility.domain.test.dto.req.TestReqDTO;
import com.capstone.fertility.domain.test.dto.res.TestResDTO;
import com.capstone.fertility.domain.test.entity.TestSession;
import com.capstone.fertility.domain.test.exception.TestException;
import com.capstone.fertility.domain.test.exception.code.TestErrorCode;
import com.capstone.fertility.domain.test.repository.TestSessionRepository;
import com.capstone.fertility.domain.test.support.SleepInputSupport;
import com.capstone.fertility.domain.user.enums.Gender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TestResultCommandServiceImpl implements TestResultCommandService {

    private static final int PSS_MIN = 0;
    private static final int PSS_MAX = 4;
    private static final int PSS_SIZE = 10;
    // PSS 구간: 0~13 낮음, 14~26 보통, 27~40 높음
    private static final int STRESS_LOW_MAX = 13;
    private static final int STRESS_MEDIUM_MAX = 26;

    private final TestSessionRepository testSessionRepository;
    private final TestResultRepository testResultRepository;
    private final AiPredictionClient aiPredictionClient;

    // 개발 단계에서 Python FastAPI AI 서버를 아직 안 만들었을 때,
    // submit API가 실패하지 않도록 AI 호출을 스킵하는 플래그입니다.
    @Value("${ai.prediction.enabled:false}")
    private boolean aiPredictionEnabled;

    @Override
    public TestResDTO.SubmitResult submit(Long userId, Long sessionId, TestReqDTO.Submit request) {
        // ① 세션 조회 및 소유자 검증
        TestSession session = testSessionRepository.findById(sessionId)
                .orElseThrow(() -> new TestException(TestErrorCode.SESSION_NOT_FOUND));

        if (!session.getUser().getId().equals(userId)) {
            throw new TestException(TestErrorCode.SESSION_NOT_OWNER);
        }

        if (testResultRepository.findByTestSession_Id(sessionId).isPresent()) {
            throw new TestException(TestErrorCode.RESULT_ALREADY_EXISTS);
        }

        List<Integer> pssAnswers = request.pssAnswers();
        if (pssAnswers == null || pssAnswers.size() != PSS_SIZE) {
            throw new TestException(TestErrorCode.INVALID_PSS_ANSWERS);
        }
        for (Integer v : pssAnswers) {
            if (v == null || v < PSS_MIN || v > PSS_MAX) {
                throw new TestException(TestErrorCode.INVALID_PSS_ANSWERS);
            }
        }

        SleepInputSupport.validateRequiredPair(session.getSleepHours(), session.getSleepMinutes());
        validateSessionProfile(session);

        // ② PSS 총점 및 구간 판별 → TestSession 최종 데이터 업데이트 및 완료
        int stressScore = pssAnswers.stream().mapToInt(Integer::intValue).sum();
        String stressLevel = resolveStressLevel(stressScore);

        session.updateFinalDataAndComplete(
                session.getSleepHours(),
                session.getSleepMinutes(),
                session.getNumBioKid(),
                session.getSexFreq(),
                session.getHasSex12Mo(),
                session.getSmokeStatus(),
                session.getDrinkStatus(),
                session.getBingeStatus(),
                stressScore,
                stressLevel
        );
        testSessionRepository.save(session);

        // ③ AI 서버 호출 (성별 분기)
        // - 남성: 한국어 범주 문자열 → AI 스케일 0,1,2 (AiLifestyleCategoryMapper)
        // - 여성: smoke_level / binge12 정수를 동일 스케일로 정규화, 음주는 drink_status 문자열 또는 0
        if (session.getGender() == null) {
            throw new TestException(TestErrorCode.INVALID_REQUEST);
        }
        Gender sessionGender = session.getGender();

        // AI 서버가 아직 준비되지 않은 로컬/개발 환경에서는 더미 결과로 정상 응답합니다.
        // 위험 요인 없음 상태(top_factors=[])와 동일한 형태로 내려보내, 프론트가 일관된 분기 처리할 수 있게 합니다.
        if (!aiPredictionEnabled) {
            int dummyScore = 0;
            RiskLevel dummyRisk = RiskLevel.determineLevel(dummyScore);
            TestResult dummy = TestResult.builder()
                    .testSession(session)
                    .user(session.getUser())
                    .aiScore(dummyScore)
                    .riskLevel(dummyRisk)
                    .topFactors(Collections.emptyList())
                    .build();

            TestResult saved = testResultRepository.save(dummy);
            return TestResDTO.SubmitResult.builder()
                    .resultId(saved.getId())
                    .aiScore(saved.getAiScore())
                    .riskLevel(saved.getRiskLevel())
                    .topFactors(saved.getTopFactors())
                    .build();
        }

        Integer smoke30;
        Integer drink12;
        Integer binge12Score;
        if (sessionGender == Gender.M) {
            smoke30 = AiLifestyleCategoryMapper.mapSmokeStatusToAi(session.getSmokeStatus());
            drink12 = AiLifestyleCategoryMapper.mapDrinkStatusToAi(session.getDrinkStatus());
            binge12Score = AiLifestyleCategoryMapper.mapBingeStatusToAi(session.getBingeStatus());
        } else {
            smoke30 = AiLifestyleCategoryMapper.femaleSmokeForAi(
                    session.getSmokeLevel(), session.getCigarettesPerDay());
            drink12 = AiLifestyleCategoryMapper.mapDrinkStatusToAi(session.getDrinkStatus());
            binge12Score = AiLifestyleCategoryMapper.femaleBingeForAi(
                    session.getBinge12(), session.getBingeDaysPerYear());
        }

        AiPredictionReqDTO.Request aiRequest = AiPredictionReqDTO.Request.builder()
                .age(session.getAge())
                .height(session.getHeight())
                .weight(session.getWeight())
                .menarcheAge(session.getMenarcheAge())
                .parity(session.getParity())
                .pcos(session.getPcos())
                .endo(session.getEndo())
                .uf(session.getUf())
                .pid(session.getPid())
                .chlam(session.getChlam())
                .gon(session.getGon())
                .smoke30(smoke30)
                .drink12(drink12)
                .binge12Score(binge12Score)
                .numBioKid(sessionGender == Gender.M ? session.getNumBioKid() : null)
                .sexFreq(sessionGender == Gender.M ? session.getSexFreq() : null)
                .hasSex12Mo(sessionGender == Gender.M
                        ? (session.getHasSex12Mo() != null && session.getHasSex12Mo() ? 1 : 0)
                        : null)
                .sleepHours(SleepInputSupport.roundSleepHoursForAi(
                        session.getSleepHours().intValue(),
                        session.getSleepMinutes().intValue()))
                .stressScore(session.getStressScore())
                .stressLevel(session.getStressLevel())
                .build();

        AiPredictionResDTO.Response aiWrapper = aiPredictionClient.predict(aiRequest, sessionGender);
        AiPredictionResDTO.ResultPayload aiResult = aiWrapper.result();

        int aiScore = aiResult.resolvedAiScore();
        // AI 서버(SHAP)가 산출한 위험 요인 전체를 그대로 보존한다.
        // 빈 문자열/공백/null은 표시 품질을 위해 사전에 걸러낸다.
        List<String> factors = aiResult.resolvedTopFactors().stream()
                .filter(f -> f != null && !f.isBlank())
                .map(String::trim)
                .toList();

        RiskLevel riskLevel = RiskLevel.determineLevel(aiScore);

        TestResult result = TestResult.builder()
                .testSession(session)
                .user(session.getUser())
                .aiScore(aiScore)
                .riskProbability(aiResult.riskProbability())
                .riskLevel(riskLevel)
                .topFactors(factors)
                .build();
        TestResult saved = testResultRepository.save(result);

        return TestResDTO.SubmitResult.builder()
                .resultId(saved.getId())
                .aiScore(saved.getAiScore())
                .riskProbability(saved.getRiskProbability())
                .riskLevel(saved.getRiskLevel())
                .topFactors(saved.getTopFactors())
                .build();
    }

    private void validateSessionProfile(TestSession session) {
        Integer age = session.getAge();
        Double height = session.getHeight();
        Double weight = session.getWeight();
        if (age == null || age < 1 || height == null || height <= 0 || weight == null || weight <= 0) {
            throw new TestException(TestErrorCode.INCOMPLETE_SESSION_PROFILE);
        }
    }

    private String resolveStressLevel(int score) {
        if (score <= STRESS_LOW_MAX) return "LOW";
        if (score <= STRESS_MEDIUM_MAX) return "NORMAL";
        return "HIGH";
    }

}

