package com.example.givy.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name="user_device_token")
public class UserDeviceToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userDeviceTokenId;

    @Column(nullable = false, unique = true)
    private String deviceToken; // 실제 FCM 기기 토큰
    
    @Column(nullable=false, unique=true)
    private String deviceId;    //기기 고유 번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users users;

    /* 로그인 시 사용자 기기 토큰 업데이트 */
    public void updateToken(String newToken) {
        this.deviceToken = newToken;
    }
}