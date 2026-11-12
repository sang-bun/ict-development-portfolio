package com.example.givy.domain.settings.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class SettingResDTO {

    /* 05-04 알림 설정 조회 */
    @Getter
    @Builder
    @AllArgsConstructor
    public static class NotificationSettingDTO {

        private Boolean guideNotificationEnabled;
        private Boolean challengeNotificationEnabled;
    }
}
