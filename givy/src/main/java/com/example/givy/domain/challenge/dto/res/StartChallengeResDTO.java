package com.example.givy.domain.challenge.dto.res;

import com.example.givy.domain.challenge.enums.Status;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class StartChallengeResDTO {
    @Getter
    @Builder
    public static class StartChallengeInfoDTO{
        private Long startChallengeId;
        private Status status;
    }

    @Getter
    @Builder
    public static class StartChallengeStatusDTO{
        private Long startChallengeId;
        private Long productId;
        private Status status;
        private LocalDateTime startedAt;    //createdAt
    }
}
