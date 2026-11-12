package com.example.givy.global.oauth.dto.res;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

// Google tokeninfo API 응답 형태
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GoogleIdTokenResponse {
    @JsonProperty("sub")
    private String sub;      // 구글 ID (Primary Key)
    
    @JsonProperty("email")
    private String email;    // 이메일
    
    @JsonProperty("name")
    private String name;     // 이름
    
    @JsonProperty("picture")
    private String picture;  // 프로필 사진
    
    @JsonProperty("aud")
    private String aud;      // 클라이언트 ID (검증용)
    
    @JsonProperty("exp")
    private Long exp;        // 만료 시간
    
    @JsonProperty("iss")
    private String iss;      // 발급자 (검증용)
}
