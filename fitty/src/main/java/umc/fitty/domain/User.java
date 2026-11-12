package umc.fitty.domain;

import jakarta.persistence.*;
import lombok.*;
import umc.fitty.domain.common.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "user", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"email"})
})
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String kakaoId;

    @Column(nullable = false, length = 100, unique = true)
    private String email;

    /** 카카오 원본 프로필 */
    @Column(length = 50)
    private String kakaoNickname;

    @Column(length = 255)
    private String kakaoProfileImageUrl;

    /** 앱 전용 프로필 (사용자 커스텀) */
    @Column(length = 50)
    private String nickname;

    @Column(length = 255)
    private String profileImageUrl;

    @Column(nullable = false)
    private boolean isDeleted;

    private LocalDateTime deletedAt;

    @Column(length = 1000)
    private String withdrawReason;

    @Column(length = 2000)
    private String withdrawFeedback;

    public boolean isActive() {
        return !this.isDeleted;
    }

    public void withdraw(String reason, String feedback) {
        if (this.isDeleted) return;
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
        this.withdrawReason = reason;
        this.withdrawFeedback = feedback;
    }

    // ====== 동기화 및 업데이트 메서드 ======

    /**
     * 카카오 로그인 시점에 원본 값만 갱신
     */
    public void updateFromKakao(String email, String kakaoNickname, String kakaoProfileImageUrl) {
        if (email != null) this.email = email;
        if (kakaoNickname != null) this.kakaoNickname = kakaoNickname;
        if (kakaoProfileImageUrl != null) this.kakaoProfileImageUrl = kakaoProfileImageUrl;
    }

    /**
     * 앱 내 프로필 수정
     * null이 들어오면 "카카오 값으로 되돌리기"
     */
    public void updateProfile(String nickname, String profileImageUrl) {
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
    }

    /** 현재 표시할 닉네임 (앱 값이 우선) */
    public String getEffectiveNickname() {
        return (this.nickname != null) ? this.nickname : this.kakaoNickname;
    }

    /** 현재 표시할 프로필 이미지 (앱 값이 우선) */
    public String getEffectiveProfileImageUrl() {
        return (this.profileImageUrl != null) ? this.profileImageUrl : this.kakaoProfileImageUrl;
    }
}
