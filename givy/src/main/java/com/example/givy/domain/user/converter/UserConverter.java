package com.example.givy.domain.user.converter;

import com.example.givy.domain.challenge.entity.Stamp;
import com.example.givy.domain.user.dto.req.UserReqDTO;
import com.example.givy.domain.user.dto.req.UserSecuritiesReqDTO;
import com.example.givy.domain.user.dto.res.UserResDTO;
import com.example.givy.domain.user.dto.res.UserSecuritiesResDTO;
import com.example.givy.domain.user.entity.UserSecuritiesAccount;
import com.example.givy.domain.user.entity.UserStamp;
import com.example.givy.domain.user.entity.Users;
import com.example.givy.domain.user.enums.Role;
import com.example.givy.domain.user.enums.SocialType;

import java.util.ArrayList;
import java.util.List;

import java.util.List;

public class UserConverter {

    //dto -> entity
    public static Users toEntity(UserReqDTO.UserSignupDTO dto, String encodedPw) {
        return Users.builder()
                .email(dto.getEmail())
                .password(encodedPw)
                .name(dto.getName())
                .birth(dto.getBirthDate()) // DTO의 birthDate를 엔티티의 birth에 매핑
                .nickname(dto.getNickname())
                .language(dto.getLanguage())
                .role(Role.USER)
                .build();
    }


    //entity -> userInfoDto
    public static UserResDTO.UserInfoDTO toDTO(Users user) {
        return UserResDTO.UserInfoDTO.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .username(user.getName())
                .build();
    }

    // 로그인 응답 DTO (token + userInfo)
    public static UserResDTO.UserLoginResDTO toLoginDTO(String accessToken, String refreshToken ,Users user) {
        return UserResDTO.UserLoginResDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(toDTO(user))
                .build();
    }

    public static UserResDTO.UserMyPageDTO toMyPageDTO(Users user) {

        List<UserResDTO.SocialAccountDTO> socialAccounts = new ArrayList<>();

        for (SocialType type : SocialType.values()) {

            boolean connected = type == user.getSocialType();

            socialAccounts.add(
                    UserResDTO.SocialAccountDTO.builder()
                            .provider(type)
                            .connected(connected)
                            .email(connected ? user.getEmail() : null)
                            .connectedAt(connected ? user.getConnectedAt() : null)
                            .build()
            );
        }

        return UserResDTO.UserMyPageDTO.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .language(user.getLanguage())
                .socialAccounts(socialAccounts)
                .build();
    }

    // RegisterSecuritiesAccountDTO -> Entity
    public static List<UserSecuritiesAccount> toUserSecuritiesListEntity(Users user, UserSecuritiesReqDTO.RegisterSecuritiesDTO dto) {
        return dto.getSecuritiesList().stream()
                .map(name -> UserSecuritiesAccount.builder()
                        .securitiesName(name)
                        .users(user)
                        .build())
                .toList();
    }

    public static UserSecuritiesAccount toUserSecuritiesEntity(Users user, String securityName) {
        return UserSecuritiesAccount.builder()
                .securitiesName(securityName)
                .users(user)
                .build();
    }

    //Entity -> UserSecuritiesListDTO
    public static UserSecuritiesResDTO.UserSecuritiesListDTO toUserSecuritiesListDTO(List<UserSecuritiesAccount> entities) {
        return UserSecuritiesResDTO.UserSecuritiesListDTO.builder()
                .securitiesList(entities.stream()
                        .map(entity -> UserSecuritiesResDTO.UserSecuritiesInfoDTO.builder()
                                .accountId(entity.getAccountId())
                                .securitiesName(entity.getSecuritiesName())
                                .build())
                        .toList())
                .build();
    }

    //Entity -> AgeVerificationResDTO
    public static UserResDTO.AgeVerificationResDTO toAgeVerificationResDTO(Users user, boolean isAdult){
        return UserResDTO.AgeVerificationResDTO.builder()
                .userId(user.getUserId())
                .isAdult(isAdult)
                .birth(user.getBirth())
                .build();
    }

    //스탬프 entity
    public static UserStamp toUserStampEntity(Users user, Stamp stamp){
        return UserStamp.builder()
                .users(user)
                .stamp(stamp)
                .build();
    }


}
