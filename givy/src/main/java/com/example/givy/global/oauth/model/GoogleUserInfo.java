package com.example.givy.global.oauth.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GoogleUserInfo {
    private String googleId; // 구글의 'sub'는 String입니다.
    private String email;
    private String nickname;
    private String profileImage;
}