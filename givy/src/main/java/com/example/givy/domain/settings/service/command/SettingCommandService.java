package com.example.givy.domain.settings.service.command;

import com.example.givy.domain.settings.dto.req.SettingReqDTO;

public interface SettingCommandService {

    /* 05-01 프로필 수정 */
    void updateProfile(Long userId, SettingReqDTO.UpdateProfileDTO request);

    /* 05-02 비밀번호 변경 */
    void updatePassword(Long userId, SettingReqDTO.UpdatePasswordDTO request);

    /* 05-03 투자 성향 재설정 */
    void resetTendency(Long userId);

    /* 05-05 알림 수신 설정 변경 */
    void updateNotificationSetting(
            Long userId,
            SettingReqDTO.UpdateNotificationDTO request
    );

    /* 05-06 앱 언어 변경 */
    void updateLanguage(Long userId, SettingReqDTO.UpdateLanguageDTO request);

    /* 05-08 회원 탈퇴 */
    void withdraw(Long userId);
}
