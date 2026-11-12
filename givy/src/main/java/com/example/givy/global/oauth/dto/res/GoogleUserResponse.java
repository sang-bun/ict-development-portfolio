package com.example.givy.global.oauth.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 구글 API (https://www.googleapis.com/oauth2/v3/userinfo)의 응답 형태
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GoogleUserResponse {
    private String sub;      // 구글 ID (Primary Key)
    private String name;     // 이름
    private String email;    // 이메일
    private String picture;  // 프로필 사진
}