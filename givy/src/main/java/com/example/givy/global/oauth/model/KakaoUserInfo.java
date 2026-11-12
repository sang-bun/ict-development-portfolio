package com.example.givy.global.oauth.model;

import lombok.Builder;
import lombok.Getter;

//우리 서버에서 실제로 쓰는 통합된 사용자 정보
@Getter
@Builder
public class KakaoUserInfo {

    private Long kakaoId;
    private String email;
    private String nickname;
}
