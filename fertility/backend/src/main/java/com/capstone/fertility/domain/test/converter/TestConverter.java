package com.capstone.fertility.domain.test.converter;

import com.capstone.fertility.domain.test.dto.res.TestResDTO;
import com.capstone.fertility.domain.test.entity.TestSession;
import com.capstone.fertility.domain.test.enums.TestSessionStatus;
import com.capstone.fertility.domain.user.enums.Gender;
import com.capstone.fertility.domain.user.entity.User;
import com.capstone.fertility.global.ai.mapping.LifestyleDisplayLabels;

public class TestConverter {

    /**
     * 사용자와 연결된 새 검사 세션 엔티티 생성 (초기 상태 IN_PROGRESS, 입력 필드는 모두 null)
     */
    public static TestSession toTestSession(User user, Gender gender) {
        return TestSession.builder()
                .user(user)
                .gender(gender)
                .status(TestSessionStatus.IN_PROGRESS)
                .build();
    }

    /**
     * IDE 린터가 toTestSession(User, Gender)를 시그니처로 인식하지 못할 때를 대비한 명시적 팩토리 메서드.
     */
    public static TestSession toTestSessionWithGender(User user, Gender gender) {
        return toTestSession(user, gender);
    }

    /**
     * 검사 세션 생성 응답 DTO로 변환 (sessionId 반환용)
     */
    public static TestResDTO.CreateSessionResDTO toCreateSessionResDTO(TestSession session) {
        return TestResDTO.CreateSessionResDTO.builder()
                .sessionId(session.getId())
                .build();
    }

    /**
     * 검사 세션 → 상세 조회 DTO (임시 저장 복구용)
     */
    public static TestResDTO.SessionDetailDTO toSessionDetailDTO(TestSession session) {
        return TestResDTO.SessionDetailDTO.builder()
                .sessionId(session.getId())
                .status(session.getStatus())
                .currentStep(session.getCurrentStep())
                .gender(session.getGender())
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
                .smokeLevel(session.getSmokeLevel())
                .binge12(session.getBinge12())
                .cigarettesPerDay(session.getCigarettesPerDay())
                .bingeDaysPerYear(session.getBingeDaysPerYear())
                .sleepHours(session.getSleepHours())
                .sleepMinutes(session.getSleepMinutes())
                .numBioKid(session.getNumBioKid())
                .sexFreq(session.getSexFreq())
                .hasSex12Mo(session.getHasSex12Mo())
                .smokeStatus(session.getSmokeStatus())
                .drinkStatus(session.getDrinkStatus())
                .bingeStatus(session.getBingeStatus())
                .smokeLabel(LifestyleDisplayLabels.smokeLabel(
                        session.getGender(),
                        session.getSmokeStatus(),
                        session.getSmokeLevel(),
                        session.getCigarettesPerDay()))
                .drinkLabel(LifestyleDisplayLabels.drinkLabel(session.getDrinkStatus()))
                .bingeLabel(LifestyleDisplayLabels.bingeLabel(
                        session.getGender(),
                        session.getBingeStatus(),
                        session.getBinge12(),
                        session.getBingeDaysPerYear()))
                .pssAnswers(session.getPssAnswers() != null && !session.getPssAnswers().isEmpty()
                        ? session.getPssAnswers() : null)
                .stressScore(session.getStressScore())
                .stressLevel(session.getStressLevel())
                .build();
    }
}

