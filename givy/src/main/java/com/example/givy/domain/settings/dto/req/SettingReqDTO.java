package com.example.givy.domain.settings.dto.req;

import com.example.givy.domain.user.enums.Language;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

public class SettingReqDTO {

    /* 05-01 프로필 수정 */
    @Getter
    public static class UpdateProfileDTO {

        @NotBlank
        private String nickname;

        @NotNull
        private Language language;

        private String profileImageUrl;
    }

    /* 05-02 비밀번호 변경 */
    @Getter
    public static class UpdatePasswordDTO {

        @NotBlank
        private String currentPassword;

        @NotBlank
        private String newPassword;
    }

    /* 05-05 알림 수신 설정 변경 */
    @Getter
    public static class UpdateNotificationDTO {

        @NotNull
        private Boolean guideNotificationEnabled;

        @NotNull
        private Boolean challengeNotificationEnabled;
    }

    /* 05-06 앱 언어 변경 */
    @Getter
    public static class UpdateLanguageDTO {

        @NotNull
        private Language language;
    }
}
