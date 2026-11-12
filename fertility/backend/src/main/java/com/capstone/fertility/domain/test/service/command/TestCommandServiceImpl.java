package com.capstone.fertility.domain.test.service.command;

import com.capstone.fertility.domain.test.converter.TestConverter;
import com.capstone.fertility.domain.test.dto.req.TestReqDTO;
import com.capstone.fertility.domain.test.dto.res.TestResDTO;
import com.capstone.fertility.domain.test.entity.TestSession;
import com.capstone.fertility.domain.test.enums.TestSessionStatus;
import com.capstone.fertility.domain.test.exception.TestException;
import com.capstone.fertility.domain.test.exception.code.TestErrorCode;
import com.capstone.fertility.domain.test.repository.TestSessionRepository;
import com.capstone.fertility.domain.test.support.SleepInputSupport;
import com.capstone.fertility.domain.user.enums.Gender;
import com.capstone.fertility.global.ai.mapping.AiLifestyleCategoryMapper;
import com.capstone.fertility.domain.user.entity.User;
import com.capstone.fertility.domain.user.exception.UserException;
import com.capstone.fertility.domain.user.exception.code.UserErrorCode;
import com.capstone.fertility.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TestCommandServiceImpl implements TestCommandService {

    private final TestSessionRepository testSessionRepository;
    private final UserRepository userRepository;

    @Override
    public TestResDTO.CreateSessionResDTO start(Long userId, TestReqDTO.Start request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_ID_NOT_FOUND));

        if (request == null || request.gender() == null) {
            throw new TestException(TestErrorCode.INVALID_REQUEST);
        }

        Gender gender = request.gender();
        TestSession session = TestConverter.toTestSessionWithGender(user, gender);
        TestSession saved = testSessionRepository.save(session);
        return TestConverter.toCreateSessionResDTO(saved);
    }

    @Override
    public void saveMaleStep(Long userId, Long sessionId, TestReqDTO.MaleStepSave request) {
        TestSession session = testSessionRepository.findById(sessionId)
                .orElseThrow(() -> new TestException(TestErrorCode.SESSION_NOT_FOUND));

        if (!session.getUser().getId().equals(userId)) {
            throw new TestException(TestErrorCode.SESSION_NOT_OWNER);
        }
        if (session.getStatus() != TestSessionStatus.IN_PROGRESS) {
            throw new TestException(TestErrorCode.SESSION_ALREADY_COMPLETED);
        }

        if (session.getGender() == null) {
            throw new TestException(TestErrorCode.INVALID_REQUEST);
        }
        if (session.getGender() != com.capstone.fertility.domain.user.enums.Gender.M) {
            throw new TestException(TestErrorCode.SESSION_GENDER_MISMATCH);
        }

        SleepInputSupport.validateOptionalPair(request.sleepHours(), request.sleepMinutes());

        session.updateMaleStepData(
                request.step(),
                request.age(),
                request.height(),
                request.weight(),
                request.chlam(),
                request.gon(),
                request.numBioKid(),
                request.sexFreq(),
                request.hasSex12Mo(),
                request.smokeStatus(),
                request.drinkStatus(),
                request.bingeStatus(),
                request.sleepHours(),
                request.sleepMinutes()
        );
        testSessionRepository.save(session);
    }

    @Override
    public void saveFemaleStep(Long userId, Long sessionId, TestReqDTO.FemaleStepSave request) {
        TestSession session = testSessionRepository.findById(sessionId)
                .orElseThrow(() -> new TestException(TestErrorCode.SESSION_NOT_FOUND));

        if (!session.getUser().getId().equals(userId)) {
            throw new TestException(TestErrorCode.SESSION_NOT_OWNER);
        }
        if (session.getStatus() != TestSessionStatus.IN_PROGRESS) {
            throw new TestException(TestErrorCode.SESSION_ALREADY_COMPLETED);
        }

        if (session.getGender() == null) {
            throw new TestException(TestErrorCode.INVALID_REQUEST);
        }
        if (session.getGender() != com.capstone.fertility.domain.user.enums.Gender.F) {
            throw new TestException(TestErrorCode.SESSION_GENDER_MISMATCH);
        }

        SleepInputSupport.validateOptionalPair(request.sleepHours(), request.sleepMinutes());

        String resolvedDrinkStatus = AiLifestyleCategoryMapper.resolveFemaleDrinkStatus(
                request.drinkStatus(), request.drinkLevel());

        session.updateFemaleStepData(
                request.step(),
                request.age(),
                request.height(),
                request.weight(),
                request.chlam(),
                request.gon(),
                request.menarcheAge(),
                request.parity(),
                request.pcos(),
                request.endo(),
                request.uf(),
                request.pid(),
                request.smokeLevel(),
                request.binge12(),
                resolvedDrinkStatus,
                request.cigarettesPerDay(),
                request.bingeDaysPerYear(),
                request.sleepHours(),
                request.sleepMinutes(),
                request.pssAnswers()
        );
        testSessionRepository.save(session);
    }
}
