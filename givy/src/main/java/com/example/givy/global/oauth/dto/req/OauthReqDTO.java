package com.example.givy.global.oauth.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

public class OauthReqDTO {

    @Getter
    public static class GoogleIdTokenDTO {
        @NotBlank(message = "ID 토큰은 필수입니다.")
        private String idToken;
    }
}
