package com.capstone.fertility.domain.user.entity;

import com.capstone.fertility.domain.user.enums.Gender;
import com.capstone.fertility.domain.user.enums.LoginType;
import com.capstone.fertility.domain.user.enums.UserStatus;
import com.capstone.fertility.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 보호 (Lombok)
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "kakao_id", unique = true)
    private Long kakaoId;

    @Enumerated(EnumType.STRING)
    @Column(name = "login_type", length = 20, nullable = false)
    private LoginType loginType;

    @Column(name = "email", length = 100, unique = true)
    private String email;

    @Column(name = "password", length = 255)
    private String password;

    @Column(name = "partner_code", length = 50, unique = true)
    private String partnerCode;

    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

    @Column(nullable = false, length = 50)
    private String nickname;

    /** 홈 인사·설정용 표시 이름(계정 nickname과 분리). 1~20자, trim 저장. */
    @Column(name = "display_name", length = 20)
    private String displayName;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Gender gender;

    @Column(name = "birth_year")
    private Integer birthYear;

    @Builder.Default
    @Column(name = "current_level")
    private int currentLevel = 1;

    @Builder.Default
    @Column(name = "current_exp")
    private int currentExp = 0;

    @Column(name = "is_terms_agreed", nullable = false)
    private boolean isTermsAgreed;

    @Column(name = "last_login_date")
    private LocalDateTime lastLoginDate;

    @Column(name = "last_mission_date")
    private LocalDateTime lastMissionDate;

    /** KST 기준 일일 웰니스 미션 EXP 보상 카운터가 유효한 날짜 */
    @Column(name = "daily_wellness_reward_date")
    private LocalDate dailyWellnessRewardDate;

    /** 해당 일에 +5 EXP를 받은 웰니스 미션 완료 횟수 (최대 3) */
    @Column(name = "daily_wellness_reward_count")
    private Integer dailyWellnessRewardCount;

    /** KST 기준, 마지막으로 미접속 페널티(-10)를 적용한 날짜 (같은 날 중복 적용 방지) */
    @Column(name = "last_inactivity_penalty_date")
    private LocalDate lastInactivityPenaltyDate;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(length = 20)
    private UserStatus status = UserStatus.ACTIVE;

    // 파트너(자기 참조 1:1 관계) - 반드시 지연 로딩(LAZY) 적용!
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id")
    private User partner;

    // --- 비즈니스 편의 메서드 ---

    /**
     * 마지막 로그인 시간 업데이트
     */
    public void updateLastLoginDate() {
        this.lastLoginDate = LocalDateTime.now();
    }

    /**
     * 마지막 미션 완료 시각 갱신
     */
    public void updateLastMissionDate() {
        this.lastMissionDate = LocalDateTime.now();
    }

    /**
     * 회원가입 시 추가 정보 입력 (온보딩)
     */
    public void updateProfile(Gender gender, Integer birthYear, boolean isTermsAgreed) {
        this.gender = gender;
        this.birthYear = birthYear;
        this.isTermsAgreed = isTermsAgreed;
    }

    public void updateProfile(String nickname, String profileImageUrl) {
        patchProfile(nickname, profileImageUrl, null, null);
    }

    /**
     * PATCH /users/me — null 필드는 변경하지 않음.
     */
    public void patchProfile(String nickname, String profileImageUrl, String displayName, Gender gender) {
        if (nickname != null) {
            this.nickname = nickname;
        }
        if (profileImageUrl != null) {
            this.profileImageUrl = profileImageUrl;
        }
        if (displayName != null) {
            String trimmed = displayName.trim();
            this.displayName = trimmed.isEmpty() ? null : trimmed;
        }
        if (gender != null) {
            this.gender = gender;
        }
    }
    /**
     * 파트너 연결
     */
    public void linkPartner(User partner) {
        this.partner = partner;
        // 양방향 세팅이 필요하다면 partner.setPartner(this) 등 추가 구현 가능
    }

    public void clearPartner() {
        this.partner = null;
    }

    public boolean isActive() {
        return this.status == UserStatus.ACTIVE;
    }

    /** 소프트 삭제: 계정 비활성화 및 재로그인용 식별자 해제 */
    public void withdraw() {
        this.status = UserStatus.DELETED;
        this.partner = null;
        this.kakaoId = null;
        if (this.email != null && !this.email.isBlank()) {
            this.email = "withdrawn." + this.id + "." + System.currentTimeMillis() + "@deleted.local";
        }
    }

    /** 최대 레벨 */
    private static final int MAX_LEVEL = 5;

    /**
     * 레벨업에 필요한 EXP. requiredExpForLevelUp[L] = Lv.L에서 Lv.L+1로 가는 데 필요한 EXP.
     * 명세: Lv1→Lv2:100, Lv2→Lv3:150, Lv3→Lv4:200, Lv4→Lv5:250.
     * 인덱스 0은 사용하지 않음.
     */
    private static final int[] REQUIRED_EXP = {0, 100, 150, 200, 250};

    /**
     * 경험치 획득 및 레벨업 처리 (RPG 식 바 시스템).
     * - 현재 레벨의 임계 EXP를 채우면 레벨업, 초과분은 다음 레벨 EXP로 이월된다.
     * - Lv.5 도달 시 더 이상 레벨업하지 않고, 추가 경험치는 currentExp에 누적되지 않는다 (현재 레벨에서 정지).
     * - 음수(페널티) 입력도 허용. 단, currentExp가 0 미만으로 떨어지지 않도록 클램프.
     *
     * @return 이 호출로 레벨이 한 번이라도 올랐으면 true
     */
    public boolean addExp(int exp) {
        int levelBefore = this.currentLevel;
        this.currentExp += exp;

        if (this.currentExp < 0) {
            this.currentExp = 0;
        }

        while (this.currentLevel < MAX_LEVEL && this.currentExp >= REQUIRED_EXP[this.currentLevel]) {
            this.currentExp -= REQUIRED_EXP[this.currentLevel];
            this.currentLevel += 1;
        }

        // 최대 레벨에 도달하면 EXP 바는 0으로 고정 (다음 진행이 없음)
        if (this.currentLevel >= MAX_LEVEL) {
            this.currentExp = 0;
        }

        return this.currentLevel > levelBefore;
    }

    /**
     * 재검사 사이클: Lv.5 도달 후 새 검사를 시작했을 때 레벨/EXP를 초기화한다.
     */
    public void resetProgressForNewCycle() {
        this.currentLevel = 1;
        this.currentExp = 0;
    }

    /**
     * 외부 노출용: 현재 레벨에서 다음 레벨로 가는 데 필요한 총 EXP.
     */
    public int getRequiredExpForCurrentLevel() {
        if (this.currentLevel >= MAX_LEVEL) {
            return 0;
        }
        return REQUIRED_EXP[this.currentLevel];
    }

    /** KST 기준으로 일일 웰니스 보상 카운터를 오늘 날짜에 맞게 초기화한다. */
    public void alignDailyWellnessRewardCounter(LocalDate kstToday) {
        if (dailyWellnessRewardDate == null || !dailyWellnessRewardDate.equals(kstToday)) {
            this.dailyWellnessRewardDate = kstToday;
            this.dailyWellnessRewardCount = 0;
        }
    }

    /** 오늘 아직 +5 EXP를 3번 미만 받았는지 */
    public boolean hasRemainingDailyWellnessExpRewards() {
        return effectiveDailyWellnessRewardCount() < 3;
    }

    public void incrementDailyWellnessExpRewards() {
        this.dailyWellnessRewardCount = effectiveDailyWellnessRewardCount() + 1;
    }

    private int effectiveDailyWellnessRewardCount() {
        return dailyWellnessRewardCount == null ? 0 : dailyWellnessRewardCount;
    }

    public LocalDate getLastInactivityPenaltyDate() {
        return lastInactivityPenaltyDate;
    }

    public void setLastInactivityPenaltyDate(LocalDate lastInactivityPenaltyDate) {
        this.lastInactivityPenaltyDate = lastInactivityPenaltyDate;
    }
}
