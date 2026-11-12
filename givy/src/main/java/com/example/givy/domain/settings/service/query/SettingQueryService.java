package com.example.givy.domain.settings.service.query;

import com.example.givy.domain.settings.dto.res.SettingResDTO;

public interface SettingQueryService {

    /* 05-04 알림 설정 조회 */
    SettingResDTO.NotificationSettingDTO getNotificationSetting(Long userId);
}
