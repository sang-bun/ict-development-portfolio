package com.capstone.fertility.domain.user.dto.res;

import lombok.Builder;

public class UserResDTO {

    @Builder
    public record LoginResDTO (
        String accessToken,
        String refreshToken,
        UserInfoDTO user
    ) {}

    @Builder
    public record UserInfoDTO (
        Long userId,
        String nickname,
        /** 홈 인사·설정 표시 이름. 미설정 시 null */
        String displayName,
        String profileImageUrl,
        /** M | F. 미설정 시 null */
        String gender,
        boolean isTermsAgreed
    ) {}
}