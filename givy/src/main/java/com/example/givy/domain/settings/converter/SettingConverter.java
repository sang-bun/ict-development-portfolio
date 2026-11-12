package com.example.givy.domain.settings.converter;

import com.example.givy.domain.settings.dto.res.SettingResDTO;

public class SettingConverter {

    public static SettingResDTO.NotificationSettingDTO toNotificationSettingDTO(
            Boolean guideEnabled,
            Boolean challengeEnabled
    ) {
        return SettingResDTO.NotificationSettingDTO.builder()
                .guideNotificationEnabled(guideEnabled)
                .challengeNotificationEnabled(challengeEnabled)
                .build();
    }
}
