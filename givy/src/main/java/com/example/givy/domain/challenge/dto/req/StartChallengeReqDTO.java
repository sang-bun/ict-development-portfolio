package com.example.givy.domain.challenge.dto.req;

import lombok.Builder;
import lombok.Getter;

public class StartChallengeReqDTO {
    @Getter
    @Builder
    public static class joinChallengeDTO{
        private Long productId;
    }
}
