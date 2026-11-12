package com.example.givy.domain.user.dto.req;

import com.example.givy.domain.user.enums.Language;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

public class UserReqDTO {

    @Getter
    @Builder
    public static class UserLoginDTO {

        @NotNull
        @Email
        private String email;

        @NotNull
        private String password;
    }

    @Getter
    @Builder
    public static class UserSignupDTO {

        @NotNull
        @Email
        private String email;

        @NotNull
        private String password;

        @NotNull
        @Size(min = 2, max = 10, message = "2자에서 10자 사이의 이름을 입력해 주세요.")
        @Pattern(
                regexp = "^[a-zA-Z0-9가-힣]*$",
                message = "이름에 특수 문자와 공백은 포함될 수 없습니다."
        )
        private String name;

        @NotNull
        private String nickname;

        @NotNull
        private Language language;

        @NotNull
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate birthDate;

    }

    @Getter
    @Builder
    public static class UserProfileDTO{

        @NotNull
        @Size(min = 2, max = 10, message = "2자에서 10자 사이의 이름을 입력해 주세요.")
        @Pattern(
                regexp = "^[a-zA-Z0-9가-힣]*$",
                message = "이름에 특수 문자와 공백은 포함될 수 없습니다."
        )
        private String name;

        @NotNull
        private String nickname;

        @NotNull
        private Language language;

        @NotNull
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate birthDate;

        @NotNull
        private String profileImageUrl;

    }

    @Getter
    @Builder
    public static class UserLogoutDTO{
        @NotNull
        private String refreshToken;
    }

    @Getter
    @Builder
    public static class UserTokenRefreshDTO {

        @NotNull
        private String refreshToken;
    }


}
