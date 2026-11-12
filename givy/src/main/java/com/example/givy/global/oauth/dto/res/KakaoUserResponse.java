package com.example.givy.global.oauth.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KakaoUserResponse {
    private Long id;
    private KakaoAccount kakao_account;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KakaoAccount {
        private String email;
        private Profile profile;

        @Getter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Profile {
            private String nickname;
        }
    }

    /*
    JSON → DTO 변환 과정은 먼저 기본 생성자로 객체를 만든 뒤 각 필드를 setter 또는 reflection으로 주입하는 방식임.
    즉, 기본 생성자가 없으면 JSON 파싱 자체가 불가능해서 @NoArgsConstructor 쓴 것임.
     */
}

