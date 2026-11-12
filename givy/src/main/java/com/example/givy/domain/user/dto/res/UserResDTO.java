package com.example.givy.domain.user.dto.res;

import com.example.givy.domain.user.enums.Language;
import com.example.givy.domain.user.enums.Role;
import com.example.givy.domain.user.enums.SocialType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class UserResDTO {

    @Getter
    @Builder
    public static class UserInfoDTO{

        @NotNull
        private Long userId;

        @NotNull
        private String username;

        @Email
        @NotNull
        private String email;

    }
    @Getter
    @Builder
    public static class UserDetailDTO{

        @NotNull
        private Long userId;

        @NotNull
        private String username;

        @Email
        @NotNull
        private String email;

        @NotNull
        private Role role;

    }

    @Getter
    @Builder
    public static class UserLoginResDTO {
        @NotNull
        private String accessToken;
        @NotNull
        private String refreshToken;
        private UserInfoDTO user;
    }

    @Getter
    @Builder
    public static class UserTokenRefreshResDTO {

        @NotNull
        private String accessToken;

        @NotNull
        private String refreshToken;
    }

    @Getter
    @Builder
    public static class UserMyPageDTO {

        private Long userId;
        private String email;
        private String nickname;
        private Language language;

        private List<SocialAccountDTO> socialAccounts;
    }

    @Getter
    @Builder
    public static class SocialAccountDTO {

        @NotNull
        private SocialType provider;          // KAKAO, GOOGLE, APPLE

        @NotNull
        private Boolean connected;

        private String email;

        private LocalDateTime connectedAt;
    }

    @Getter
    @Builder
    public static class AgeVerificationResDTO{
        private Long userId;
        private LocalDate birth;
        private boolean isAdult;
    }

}
