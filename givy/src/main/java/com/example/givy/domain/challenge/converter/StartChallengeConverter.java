package com.example.givy.domain.challenge.converter;

import com.example.givy.domain.challenge.dto.res.StartChallengeResDTO;
import com.example.givy.domain.challenge.entity.StartChallenge;
import com.example.givy.domain.challenge.enums.Status;
import com.example.givy.domain.commerce.entity.Product;
import com.example.givy.domain.user.entity.Users;

public class StartChallengeConverter {

    //dto -> entity
    public static StartChallenge toEntity(Users user, Product product){
        return StartChallenge.builder()
                .status(Status.IN_PROGRESS)
                .users(user)
                .product(product)
                .build();
    }

    //entity -> StartChallengeInfoDTO
    public static StartChallengeResDTO.StartChallengeInfoDTO toStartChallengeInfoDTO(StartChallenge challenge){
        return StartChallengeResDTO.StartChallengeInfoDTO.builder()
                .startChallengeId(challenge.getStartChallengeId())
                .status(challenge.getStatus())
                .build();
    }

    //entity -> StartChallengeStatusDTO
    public static StartChallengeResDTO.StartChallengeStatusDTO toStartChallengeStatusDTO(StartChallenge challenge){
        return StartChallengeResDTO.StartChallengeStatusDTO.builder()
                .startChallengeId(challenge.getStartChallengeId())
                .productId(challenge.getProduct().getProductId())
                .status(challenge.getStatus())
                .startedAt(challenge.getCreatedAt())
                .build();
    }

}
