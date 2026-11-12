package com.example.givy.global.oauth.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GoogleTokenResponse {
    // Google은 snake_case로 응답을 줍니다.
    private String access_token;
    private Integer expires_in;
    private String scope;
    private String token_type;
    private String id_token;
}