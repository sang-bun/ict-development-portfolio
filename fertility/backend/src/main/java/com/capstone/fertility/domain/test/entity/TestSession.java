package com.capstone.fertility.domain.test.entity;

import com.capstone.fertility.domain.test.enums.TestSessionStatus;
import com.capstone.fertility.domain.user.enums.Gender;
import com.capstone.fertility.domain.user.entity.User;
import com.capstone.fertility.global.ai.mapping.AiLifestyleCategoryMapper;
import com.capstone.fertility.global.ai.mapping.FemaleLifestyleNormalized;
import com.capstone.fertility.global.common.BaseEntity;
import com.capstone.fertility.global.common.IntListConverter;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "test_sessions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TestSession extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    // 기존에 생성된 세션 데이터가 있을 수 있으므로, 컬럼 추가 시 NOT NULL 제약으로 인한 DDL 실패를 방지합니다.
    // (세션 시작 시점(start API)에서는 반드시 gender를 채우도록 서비스에서 검증합니다.)
    @Column(name = "gender", nullable = true, length = 1)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private TestSessionStatus status = TestSessionStatus.IN_PROGRESS;

    // --- 프론트엔드 입력 필드 (모두 null 허용) ---
    @Column(name = "age")
    private Integer age;

    @Column(name = "height")
    private Double height;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "menarche_age")
    private Integer menarcheAge;

    @Column(name = "parity")
    private Integer parity;

    @Column(name = "pcos")
    private Integer pcos;

    @Column(name = "endo")
    private Integer endo;

    @Column(name = "uf")
    private Integer uf;

    @Column(name = "pid")
    private Integer pid;

    @Column(name = "chlam")
    private Integer chlam;

    @Column(name = "gon")
    private Integer gon;

    @Column(name = "smoke_level")
    private Integer smokeLevel;

    @Column(name = "binge12")
    private Integer binge12;

    /** 여성: 설문 원시값 — 하루 평균 개비 수 (smoke_level은 0~2 tier) */
    @Column(name = "cigarettes_per_day")
    private Integer cigarettesPerDay;

    /** 여성: 설문 원시값 — 최근 1년 5잔+ 폭음 일수 (binge12는 0~2 tier) */
    @Column(name = "binge_days_per_year")
    private Integer bingeDaysPerYear;

    /** 남성 전용: 생물학적 자녀 수 */
    @Column(name = "num_bio_kid")
    private Integer numBioKid;

    /** 남성 전용: 최근 4주간 성관계 횟수 */
    @Column(name = "sex_freq")
    private Integer sexFreq;

    /** 최근 1년 성관계 여부 */
    @Column(name = "has_sex_12mo")
    private Boolean hasSex12Mo;

    /** 프론트 입력용 흡연 상태 (예: NONE / SOMETIMES / DAILY) */
    @Column(name = "smoke_status", length = 20)
    private String smokeStatus;

    /** 프론트 입력용 음주 상태 */
    @Column(name = "drink_status", length = 20)
    private String drinkStatus;

    /** 프론트 입력용 폭음 상태 */
    @Column(name = "binge_status", length = 20)
    private String bingeStatus;

    /** 마지막 진행 단계 (1~9). 임시 저장/복구용 */
    @Column(name = "current_step")
    private Integer currentStep;

    /** 9번 질문: 하루 수면 시간(시) */
    @Column(name = "sleep_hours")
    private Integer sleepHours;

    /** 9번 질문: 하루 수면 시간(분, 0~59) */
    @Column(name = "sleep_minutes")
    private Integer sleepMinutes;

    /** PSS 10문항 임시 저장 (0~4 × 10개, 중간 복구용) */
    @Convert(converter = IntListConverter.class)
    @Column(name = "pss_answers_temp", columnDefinition = "text")
    private List<Integer> pssAnswers;

    /** PSS 스트레스 설문 총점 (0~40) */
    @Column(name = "stress_score")
    private Integer stressScore;

    /** PSS 구간 판별 결과: LOW / NORMAL / HIGH */
    @Column(name = "stress_level", length = 20)
    private String stressLevel;

    // --- 비즈니스 메서드 (데이터 변경은 여기서만) ---

    /**
     * 남성 전용 임시 저장 시 입력값 반영. null이 아닌 값만 업데이트.
     */
    public void updateMaleStepData(
            Integer step,
            Integer age,
            Double height,
            Double weight,
            Integer chlam,
            Integer gon,
            Integer numBioKid,
            Integer sexFreq,
            Boolean hasSex12Mo,
            String smokeStatus,
            String drinkStatus,
            String bingeStatus,
            Integer sleepHours,
            Integer sleepMinutes
    ) {
        if (step != null) this.currentStep = step;
        if (age != null) this.age = age;
        if (height != null) this.height = height;
        if (weight != null) this.weight = weight;
        if (chlam != null) this.chlam = chlam;
        if (gon != null) this.gon = gon;
        if (numBioKid != null) this.numBioKid = numBioKid;
        if (sexFreq != null) this.sexFreq = sexFreq;
        if (hasSex12Mo != null) this.hasSex12Mo = hasSex12Mo;
        if (smokeStatus != null) this.smokeStatus = smokeStatus;
        if (drinkStatus != null) this.drinkStatus = drinkStatus;
        if (bingeStatus != null) this.bingeStatus = bingeStatus;
        if (sleepHours != null) this.sleepHours = sleepHours;
        if (sleepMinutes != null) this.sleepMinutes = sleepMinutes;
    }

    /**
     * 여성 전용 임시 저장 시 입력값 반영. null이 아닌 값만 업데이트.
     */
    public void updateFemaleStepData(
            Integer step,
            Integer age,
            Double height,
            Double weight,
            Integer chlam,
            Integer gon,
            Integer menarcheAge,
            Integer parity,
            Integer pcos,
            Integer endo,
            Integer uf,
            Integer pid,
            Integer smokeLevel,
            Integer binge12,
            String drinkStatus,
            Integer cigarettesPerDay,
            Integer bingeDaysPerYear,
            Integer sleepHours,
            Integer sleepMinutes,
            List<Integer> pssAnswers
    ) {
        if (step != null) this.currentStep = step;
        if (age != null) this.age = age;
        if (height != null) this.height = height;
        if (weight != null) this.weight = weight;
        if (chlam != null) this.chlam = chlam;
        if (gon != null) this.gon = gon;
        if (menarcheAge != null) this.menarcheAge = menarcheAge;
        if (parity != null) this.parity = parity;
        if (pcos != null) this.pcos = pcos;
        if (endo != null) this.endo = endo;
        if (uf != null) this.uf = uf;
        if (pid != null) this.pid = pid;
        if (drinkStatus != null) this.drinkStatus = drinkStatus;
        if (sleepHours != null) this.sleepHours = sleepHours;
        if (sleepMinutes != null) this.sleepMinutes = sleepMinutes;
        if (pssAnswers != null && !pssAnswers.isEmpty()) this.pssAnswers = pssAnswers;

        FemaleLifestyleNormalized smoke =
                AiLifestyleCategoryMapper.normalizeFemaleSmokeInput(smokeLevel, cigarettesPerDay);
        if (smoke != null) {
            this.smokeLevel = smoke.tier();
            this.cigarettesPerDay = smoke.rawQuantity();
        }

        FemaleLifestyleNormalized binge =
                AiLifestyleCategoryMapper.normalizeFemaleBingeInput(binge12, bingeDaysPerYear);
        if (binge != null) {
            this.binge12 = binge.tier();
            this.bingeDaysPerYear = binge.rawQuantity();
        }
    }

    /**
     * 최종 제출 시 입력된 데이터와 PSS 점수를 한 번에 반영하고 세션을 완료 상태로 전환.
     */
    public void updateFinalDataAndComplete(
            Integer sleepHours,
            Integer sleepMinutes,
            Integer numBioKid,
            Integer sexFreq,
            Boolean hasSex12Mo,
            String smokeStatus,
            String drinkStatus,
            String bingeStatus,
            int stressScore,
            String stressLevel
    ) {
        this.sleepHours = sleepHours;
        this.sleepMinutes = sleepMinutes;
        this.numBioKid = numBioKid;
        this.sexFreq = sexFreq;
        this.hasSex12Mo = hasSex12Mo;
        this.smokeStatus = smokeStatus;
        this.drinkStatus = drinkStatus;
        this.bingeStatus = bingeStatus;
        this.stressScore = stressScore;
        this.stressLevel = stressLevel;
        this.status = TestSessionStatus.COMPLETED;
    }
}
