package com.example.givy.domain.home.dto.res;

import lombok.Builder;

import java.util.List;

public class HomeResDTO {

    // 전체 응답
    @Builder
    public record HomeResponseDTO(
            String nickname,
            String status,
            Object data
    ){}

    // 스타트 챌린지 전
    @Builder
    public record BeforeHomeDTO(
            String goal,
            Integer expectedAmount,
            String characterImageUrl
    ){}

    // 스타트 챌린지 후
    @Builder
    public record AfterHomeDTO(
            HomeTicketDTO ticket,
            HomeFrontCardDTO frontCard,
            HomeBackCardDTO backCard
    ){}

    // 상단
    @Builder
    public record HomeTicketDTO(
            String goal,
            String targetItem,
            String productCode,
            String securitiesFirm,
            Integer amount
    ){}

    // 하단
    @Builder
    public record HomeFrontCardDTO(
            String exchange,
            String title,
            String productName,
            Double averageRate,
            Integer rateMin,
            Integer rateMax,
            List<Long> chartData
    ){}

    // 하단 뒷면
    @Builder
    public record HomeBackCardDTO(
            String descriptionTitle,
            String description
    ){}

}
