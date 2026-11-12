package com.example.givy.domain.user.entity;

import com.example.givy.domain.guide.entity.GuideLike;
import com.example.givy.domain.guide.entity.GuideStore;
import com.example.givy.domain.recommendation.entity.Tendency;
import com.example.givy.domain.user.dto.req.UserReqDTO;
import com.example.givy.domain.user.enums.Language;
import com.example.givy.domain.user.enums.Role;
import com.example.givy.domain.user.enums.SocialType;
import com.example.givy.global.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "users")  //예약어 충돌 방지를 위한 테이블명 수정 user->users
public class Users extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "name", nullable = false, length = 10)
    @Size(min = 2, max = 10, message = "2자에서 10자 사이의 이름을 입력해 주세요.")
    @Pattern(
            regexp = "^[a-zA-Z0-9가-힣]*$",
            message = "이름에 특수 문자와 공백은 포함될 수 없습니다."
    )
    private String name;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "email", nullable = false, length = 64, unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "social_type")
    private SocialType socialType;

    @Column(name = "social_id")
    private String socialId;

    @Column(name = "connected_at")
    private LocalDateTime connectedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "language", nullable = false)
    private Language language;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    //erd 추가
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private Role role;

    @Column(name="birth")
    private LocalDate birth;

    //mapping
    @OneToMany(mappedBy = "users")
    private List<UserAsset> assets = new ArrayList<>();

    @OneToMany(mappedBy = "users")
    private List<Tendency> tendency = new ArrayList<>();

    @OneToMany(mappedBy = "users")
    private List<UserNotification> userNotification = new ArrayList<>();

    @OneToMany(mappedBy = "users")
    private List<UserStamp> userStamp = new ArrayList<>();

    @OneToMany(mappedBy = "users")
    private List<BuyHistory> buyHistory = new ArrayList<>();

    @OneToMany(mappedBy = "users")
    private List<GuideLike> guideLike = new ArrayList<>();

    @OneToMany(mappedBy = "users")
    private List<GuideStore> guideStore = new ArrayList<>();

    @OneToMany(mappedBy = "users")
    private List<UserSecuritiesAccount> userSecuritiesAccount = new ArrayList<>();

    @OneToMany(mappedBy = "users")
    private List<UserDeviceToken> userDeviceTokens = new ArrayList<>();

    // 01-04번 API - 프로필 완성을 위한 메서드
    public void completeSocialProfile(UserReqDTO.UserProfileDTO dto) {
        this.name = dto.getName();
        this.nickname = dto.getNickname();
        this.language = dto.getLanguage();
        this.profileImageUrl = dto.getProfileImageUrl();
        if (this.birth == null) {
            this.birth = dto.getBirthDate();
        }
    }

    /* 05-01 프로필 수정 */
    public void updateProfile(
            String nickname,
            Language language,
            String profileImageUrl
    ) {
        this.nickname = nickname;
        this.language = language;
        this.profileImageUrl = profileImageUrl;
    }

    /* 05-02 비밀번호 변경 */
    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    /* 05-06 앱 언어 변경 */
    public void updateLanguage(Language language) {
        this.language = language;
    }

    /* 05-08 회원 탈퇴 */
    public void withdraw(LocalDateTime now) {
        this.deletedAt = now;
    }
}
