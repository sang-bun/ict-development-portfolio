package com.capstone.fertility.domain.test.service.query;

import com.capstone.fertility.domain.result.entity.TestResult;
import com.capstone.fertility.domain.result.enums.RiskLevel;
import com.capstone.fertility.domain.result.repository.TestResultRepository;
import com.capstone.fertility.domain.test.converter.TestConverter;
import com.capstone.fertility.domain.test.dto.res.TestResDTO;
import com.capstone.fertility.domain.test.entity.TestSession;
import com.capstone.fertility.domain.test.exception.TestException;
import com.capstone.fertility.domain.test.exception.code.TestErrorCode;
import com.capstone.fertility.domain.test.repository.TestSessionRepository;
import com.capstone.fertility.domain.test.support.HealthBenchmarkSupport;
import com.capstone.fertility.domain.test.support.SleepInputSupport;
import com.capstone.fertility.domain.user.enums.Gender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TestQueryServiceImpl implements TestQueryService {

    /*
     * 중간 보고서 연령 매칭 (의도된 설계):
     * - 유병률: 만 20~64세, 5세 구간.
     * - 평균 BMI: 만 20~69세, 10년 구간(20~29 … 60~69).
     * - 수면 평균: 만 20~59세는 10년 단위, 만 60세 이상은 "60대 이상" 한 구간.
     */

    private static final double BMI_STAGE1_LOW = 25.0;
    private static final double BMI_STAGE1_HIGH = 29.9;
    private static final double BMI_STAGE2_LOW = 30.0;
    private static final double BMI_STAGE2_HIGH = 34.9;

    /**
     * 비만 유병률 표 공통 연령 구간(5세 단위, 인덱스 0~8):
     * 20~24, 25~29, 30~34, 35~39, 40~44, 45~49, 50~54, 55~59, 60~64
     */
    private static final int OBESITY_PREVALENCE_BAND_COUNT = 9;

    /** BMI 25~29.9 (1단계) 유병률 % */
    private static final Double[] OBESITY_STAGE1_FEMALE = {
            13.3, 13.1, 15.1, 17.6, 18.8, 20.8, 22.7, 23.6, 24.3
    };
    private static final Double[] OBESITY_STAGE1_MALE = {
            28.2, 35.0, 39.8, 43.0, 43.9, 44.0, 43.4, 41.3, 39.5
    };

    /** BMI 30~34.9 (2단계) 유병률 % — 조사값 넣을 때 null 대신 길이 9 배열로 교체 */
    private static final Double[] OBESITY_STAGE2_FEMALE = {
            4.3, 4.3, 4.9, 5.5, 5.2, 4.9, 4.5, 4.4, 4.3
    };
    private static final Double[] OBESITY_STAGE2_MALE = {
            8.7, 9.6, 11.1, 12.0, 10.6, 8.7, 6.7, 5.0, 3.9
    };

    /** BMI 35 이상 (3단계) 유병률 % — 조사값 넣을 때 null 대신 길이 9 배열로 교체 */
    private static final Double[] OBESITY_STAGE3_FEMALE = {
            1.68, 1.73, 1.84, 1.86, 1.43, 1.09, 0.80, 0.66, 0.54
    };
    private static final Double[] OBESITY_STAGE3_MALE = {
            3.22, 2.83, 3.14, 2.97, 2.02, 1.29, 0.74, 0.41, 0.26
    };

    private final TestSessionRepository testSessionRepository;
    private final TestResultRepository testResultRepository;

    @Override
    public TestResDTO.SessionDetailDTO getSession(Long userId, Long sessionId) {
        if (sessionId == null) {
            throw new TestException(TestErrorCode.INVALID_REQUEST);
        }
        TestSession session = testSessionRepository.findById(sessionId)
                .orElseThrow(() -> new TestException(TestErrorCode.SESSION_NOT_FOUND));

        Long sessionUserId = session.getUser().getId();
        if (userId == null || sessionUserId == null || sessionUserId.longValue() != userId.longValue()) {
            throw new TestException(TestErrorCode.SESSION_NOT_OWNER);
        }

        return TestConverter.toSessionDetailDTO(session);
    }

    @Override
    public TestResDTO.InterimReportDTO getInterimReport(Long userId, Long sessionId) {
        if (sessionId == null) {
            throw new TestException(TestErrorCode.INVALID_REQUEST);
        }
        TestSession session = testSessionRepository.findById(sessionId)
                .orElseThrow(() -> new TestException(TestErrorCode.SESSION_NOT_FOUND));

        Long sessionUserId = session.getUser().getId();
        if (userId == null || sessionUserId == null || sessionUserId.longValue() != userId.longValue()) {
            throw new TestException(TestErrorCode.SESSION_NOT_OWNER);
        }

        Integer age = session.getAge();
        Integer sleepHours = session.getSleepHours();
        Integer sleepMinutes = session.getSleepMinutes();
        Double height = session.getHeight(); // cm
        Double weight = session.getWeight(); // kg
        Gender gender = session.getGender();

        boolean sleepCalculated = age != null && sleepHours != null && sleepMinutes != null;
        String sleepAgeBand = null;
        Double sleepAvgHours = null;
        Double sleepDeltaHours = null;

        if (age != null && sleepHours != null && sleepMinutes != null) {
            HealthBenchmarkSupport.SleepPeerBenchmark sleepBand = HealthBenchmarkSupport.sleepPeerBenchmark(age);
            if (sleepBand != null) {
                sleepAgeBand = sleepBand.ageBandLabel();
                sleepAvgHours = round1(sleepBand.avgHours());
                double userSleepHours = SleepInputSupport.totalSleepHoursDecimal(sleepHours, sleepMinutes);
                sleepDeltaHours = round1(userSleepHours - sleepAvgHours);
            }
        }

        boolean obesityCalculated = age != null && gender != null && height != null && weight != null;
        Double bmi = null;
        String obesityStage = null;
        Double obesityPrevalencePct = null;
        Double ageSexMeanBmi = null;
        Double bmiDeltaVsAgeSexMeanPct = null;
        Double bmiDistanceToStageBoundary = null;
        String bmiDistanceDirection = null;

        if (age != null && gender != null && height != null && weight != null) {
            double bmiRaw = HealthBenchmarkSupport.bmi(height.doubleValue(), weight.doubleValue());
            bmi = round1(bmiRaw);

            Double meanBmi = HealthBenchmarkSupport.meanBmi(gender, age);
            if (meanBmi != null && meanBmi.doubleValue() > 0.0) {
                ageSexMeanBmi = round1(meanBmi);
                bmiDeltaVsAgeSexMeanPct = round1((bmiRaw - meanBmi) / meanBmi * 100.0);
            }

            if (bmiRaw < BMI_STAGE1_LOW) {
                obesityStage = "NONE";
                bmiDistanceToStageBoundary = round1(BMI_STAGE1_LOW - bmiRaw);
                bmiDistanceDirection = "BELOW_LOWER";
            } else if (bmiRaw <= BMI_STAGE1_HIGH) {
                obesityStage = "STAGE_1";
                obesityPrevalencePct = resolveObesityStage1PrevalencePct(gender, age);
            } else if (bmiRaw >= BMI_STAGE2_LOW && bmiRaw <= BMI_STAGE2_HIGH) {
                obesityStage = "STAGE_2";
                obesityPrevalencePct = resolveObesityStage2PrevalencePct(gender, age);
            } else {
                obesityStage = "STAGE_3";
                obesityPrevalencePct = resolveObesityStage3PrevalencePct(gender, age);
            }
        }

        Long resultId = null;
        Integer aiScore = null;
        Double riskProbability = null;
        RiskLevel riskLevel = null;
        List<String> topFactors = null;

        Optional<TestResult> savedResult = testResultRepository.findByTestSession_Id(sessionId);
        if (savedResult.isPresent()) {
            TestResult tr = savedResult.get();
            resultId = tr.getId();
            aiScore = tr.getAiScore();
            riskProbability = tr.getRiskProbability();
            riskLevel = tr.getRiskLevel();
            topFactors = tr.getTopFactors();
        }

        return TestResDTO.InterimReportDTO.builder()
                .sleepCalculated(sleepCalculated)
                .sleepAgeBand(sleepAgeBand)
                .sleepAvgHours(sleepAvgHours)
                .sleepDeltaHours(sleepDeltaHours)
                .obesityCalculated(obesityCalculated)
                .bmi(bmi)
                .obesityStage(obesityStage)
                .obesityPrevalencePct(obesityPrevalencePct)
                .ageSexMeanBmi(ageSexMeanBmi)
                .bmiDeltaVsAgeSexMeanPct(bmiDeltaVsAgeSexMeanPct)
                .bmiDistanceToStageBoundary(bmiDistanceToStageBoundary)
                .bmiDistanceDirection(bmiDistanceDirection)
                .resultId(resultId)
                .aiScore(aiScore)
                .riskProbability(riskProbability)
                .riskLevel(riskLevel)
                .topFactors(topFactors)
                .build();
    }

    private double round1(double value) {
        return HealthBenchmarkSupport.round1(value);
    }

    /**
     * 1단계 비만(BMI 25~29.9) 유병률(%).
     */
    private Double resolveObesityStage1PrevalencePct(Gender gender, int age) {
        return lookupObesityPrevalenceByStage(gender, age, OBESITY_STAGE1_FEMALE, OBESITY_STAGE1_MALE);
    }

    /**
     * 2단계 비만(BMI 30~34.9) 유병률(%).
     */
    private Double resolveObesityStage2PrevalencePct(Gender gender, int age) {
        return lookupObesityPrevalenceByStage(gender, age, OBESITY_STAGE2_FEMALE, OBESITY_STAGE2_MALE);
    }

    /**
     * 3단계 비만(BMI 35 이상) 유병률(%).
     */
    private Double resolveObesityStage3PrevalencePct(Gender gender, int age) {
        return lookupObesityPrevalenceByStage(gender, age, OBESITY_STAGE3_FEMALE, OBESITY_STAGE3_MALE);
    }

    /**
     * 성별·연령(5세 구간)에 맞는 해당 단계 유병률(%).
     *
     * @param femalePctByBand 길이 9, 순서: 20~24 … 60~64. null이면 항상 null.
     * @param malePctByBand   동일
     */
    private static Double lookupObesityPrevalenceByStage(
            Gender gender,
            int age,
            Double[] femalePctByBand,
            Double[] malePctByBand
    ) {
        int idx = obesityPrevalenceAgeBandIndex(age);
        if (idx < 0) {
            return null;
        }
        if (gender == Gender.F) {
            return valueAtPrevalenceBand(femalePctByBand, idx);
        }
        if (gender == Gender.M) {
            return valueAtPrevalenceBand(malePctByBand, idx);
        }
        return null;
    }

    /** 만 20~64세만 0~8, 그 외 -1 */
    private static int obesityPrevalenceAgeBandIndex(int age) {
        if (age >= 20 && age <= 24) return 0;
        if (age >= 25 && age <= 29) return 1;
        if (age >= 30 && age <= 34) return 2;
        if (age >= 35 && age <= 39) return 3;
        if (age >= 40 && age <= 44) return 4;
        if (age >= 45 && age <= 49) return 5;
        if (age >= 50 && age <= 54) return 6;
        if (age >= 55 && age <= 59) return 7;
        if (age >= 60 && age <= 64) return 8;
        return -1;
    }

    private static Double valueAtPrevalenceBand(Double[] row, int idx) {
        if (row == null || idx < 0 || idx >= OBESITY_PREVALENCE_BAND_COUNT || idx >= row.length) {
            return null;
        }
        return row[idx];
    }

}
