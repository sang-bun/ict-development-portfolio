package com.example.givy.domain.recommendation.converter;

import com.example.givy.domain.commerce.entity.Product;
import com.example.givy.domain.recommendation.dto.res.TendencyResDTO;
import com.example.givy.domain.recommendation.entity.RecommendationEvent;
import com.example.givy.domain.recommendation.entity.RecommendationItem;
import com.example.givy.domain.recommendation.entity.Tendency;
import com.example.givy.domain.user.entity.Users;

import java.util.List;

public class TendencyConverter {

    public static Tendency toTendency(Users users, TendencyResDTO.TendencyResultDTO resultDTO){
        return Tendency.builder()
                .investmentType(resultDTO.investmentType())
                .scoreR(resultDTO.scoreR())
                .scoreL(resultDTO.scoreL())
                .scoreT(resultDTO.scoreT())
                .totalScore(resultDTO.totalScore())
                .imageUrl(resultDTO.imageBasicUrl())
                .users(users)
                .build();
    }

    public static TendencyResDTO.TendencyResultDTO toTendencyResDTO(int scoreR, int scoreL, int scoreT, int totalScore, String investmentType, String imageUrl){
        return TendencyResDTO.TendencyResultDTO.builder()
                .scoreR(scoreR)
                .scoreL(scoreL)
                .scoreT(scoreT)
                .totalScore(totalScore)
                .investmentType(investmentType)
                .imageBasicUrl(imageUrl)
                .build();
    }

    public static TendencyResDTO.TendencyResultDTO toTendencyResultDTO(Tendency tendency) {
        return TendencyResDTO.TendencyResultDTO.builder()
                .scoreR(tendency.getScoreR())
                .scoreL(tendency.getScoreL())
                .scoreT(tendency.getScoreT())
                .totalScore(tendency.getTotalScore())
                .investmentType(tendency.getInvestmentType())
                .imageBasicUrl(tendency.getImageUrl())
                .riskLabel(tendency.getRiskLabel())
                .periodLabel(tendency.getPeriodLabel())
                .familiarityLabel(tendency.getFamiliarityLabel())
                .build();
    }

    public static TendencyResDTO.TendencyViewDTO toTendencyViewDTO(Tendency tendency){
        return TendencyResDTO.TendencyViewDTO.builder()
                .investmentType(tendency.getInvestmentType())
                .riskLabel(tendency.getRiskLabel())
                .periodLabel(tendency.getPeriodLabel())
                .familiarityLabel(tendency.getFamiliarityLabel())
                .imageBasicUrl(tendency.getImageUrl())
                .build();
    }

    public static RecommendationEvent toRecommendationEvent(Tendency tendency, Product bestProduct){
        return RecommendationEvent.builder()
                .tendency(tendency)
                .bestProduct(bestProduct)
                .build();
    }

    public static RecommendationItem toRecommendationItem(RecommendationEvent recommendationEvent, Product product){
        return RecommendationItem.builder()
                .recommendationEvent(recommendationEvent)
                .product(product)
                .build();
    }

    public static TendencyResDTO.RecommendationResultDTO toRecommendationResultDTO(String investmentType, String bestProductName, String bestProductCode){
        return TendencyResDTO.RecommendationResultDTO.builder()
                .investmentType(investmentType)
                .bestProductName(bestProductName)
                .bestProductCode(bestProductCode)
                .build();
    }

    public static TendencyResDTO.ProductOverviewDTO toProductOverviewDTO(Product product){
        return TendencyResDTO.ProductOverviewDTO.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .code(product.getCode())
                .imageUrl(product.getImageUrl())
                .tagline(product.getTagline())
                .description(product.getDescription())
                .rateAvg(product.getRateAvg())
                .build();
    }

    public static TendencyResDTO.RecommendationListDTO toRecommendationListDTO(RecommendationEvent event, Tendency tendency, List<TendencyResDTO.ProductOverviewDTO> productsDTO){
        return TendencyResDTO.RecommendationListDTO.builder()
                .recommendationEventId(event.getRecommendationEventId())
                .investmentType(tendency.getInvestmentType())
                .products(productsDTO)
                .build();
    }
}
