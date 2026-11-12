package com.capstone.fertility.global.oauth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 카카오 서버가 우리에게 주는 유저 정보(닉네임, 프사 등)를 담는 바구니
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KakaoUserResponse {
    private Long id;
    private KakaoAccount kakao_account;

    // JSON 중첩 구조를 풀기 위한 내부 클래스들
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KakaoAccount {
        private Profile profile;

        @Getter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Profile {
            private String nickname;
            private String profile_image_url;
        }
    }

    /*
    JSON → DTO 변환 과정은 먼저 기본 생성자로 객체를 만든 뒤 각 필드를 setter 또는 reflection으로 주입하는 방식임.
    즉, 기본 생성자가 없으면 JSON 파싱 자체가 불가능해서 @NoArgsConstructor 쓴 것임.
     */
}

