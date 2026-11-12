package com.capstone.fertility.global.oauth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 카카오 서버가 우리에게 주는 Access Token 응답값을 담는 바구니
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KakaoTokenResponse {
    private String token_type;
    private String access_token;
    private Integer expires_in;
    private String refresh_token;
    private Integer refresh_token_expires_in;
    private String scope;
}