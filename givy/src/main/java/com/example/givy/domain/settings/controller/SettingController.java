package com.example.givy.domain.settings.controller;

import com.example.givy.domain.settings.code.SettingSuccessCode;
import com.example.givy.domain.settings.dto.req.SettingReqDTO;
import com.example.givy.domain.settings.dto.res.SettingResDTO;
import com.example.givy.domain.settings.service.command.SettingCommandService;
import com.example.givy.domain.settings.service.query.SettingQueryService;
import com.example.givy.global.apiPayLoad.ApiResponse;
import com.example.givy.global.security.CustomPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/settings")
public class SettingController implements SettingControllerDocs {

    private final SettingCommandService settingCommandService;
    private final SettingQueryService settingQueryService;

    /* 05-01 프로필 수정 */
    @PatchMapping("/users/profile")
    @Override
    public ApiResponse<Void> updateProfile(
            @AuthenticationPrincipal CustomPrincipal principal,
            @RequestBody @Valid SettingReqDTO.UpdateProfileDTO request
    ) {
        settingCommandService.updateProfile(principal.getUserId(), request);
        return ApiResponse.onSuccess(SettingSuccessCode.PROFILE_UPDATE_SUCCESS);
    }

    /* 05-02 비밀번호 변경 */
    @PatchMapping("/users/password")
    @Override
    public ApiResponse<Void> updatePassword(
            @AuthenticationPrincipal CustomPrincipal principal,
            @RequestBody @Valid SettingReqDTO.UpdatePasswordDTO request
    ) {
        settingCommandService.updatePassword(principal.getUserId(), request);
        return ApiResponse.onSuccess(SettingSuccessCode.PASSWORD_UPDATE_SUCCESS);
    }

    /* 05-03 투자 성향 재설정 */
    @DeleteMapping("/tendency")
    @Override
    public ApiResponse<Void> resetTendency(
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        settingCommandService.resetTendency(principal.getUserId());
        return ApiResponse.onSuccess(SettingSuccessCode.TENDENCY_RESET_SUCCESS);
    }

    /* 05-04 알림 설정 조회 */
    @GetMapping("/notifications")
    @Override
    public ApiResponse<SettingResDTO.NotificationSettingDTO> getNotificationSetting(
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        return ApiResponse.onSuccess(
                SettingSuccessCode.NOTIFICATION_SETTING_FETCH_SUCCESS,
                settingQueryService.getNotificationSetting(principal.getUserId())
        );
    }

    /* 05-05 알림 수신 설정 변경 */
    @PatchMapping("/notifications")
    @Override
    public ApiResponse<Void> updateNotificationSetting(
            @AuthenticationPrincipal CustomPrincipal principal,
            @RequestBody @Valid SettingReqDTO.UpdateNotificationDTO request
    ) {
        settingCommandService.updateNotificationSetting(principal.getUserId(), request);
        return ApiResponse.onSuccess(
                SettingSuccessCode.NOTIFICATION_SETTING_UPDATE_SUCCESS
        );
    }

    /* 05-06 앱 언어 변경 */
    @PatchMapping("/users/language")
    @Override
    public ApiResponse<Void> updateLanguage(
            @AuthenticationPrincipal CustomPrincipal principal,
            @RequestBody @Valid SettingReqDTO.UpdateLanguageDTO request
    ) {
        settingCommandService.updateLanguage(principal.getUserId(), request);
        return ApiResponse.onSuccess(SettingSuccessCode.LANGUAGE_UPDATE_SUCCESS);
    }

    /* 05-08 회원 탈퇴 */
    @DeleteMapping("/users")
    @Override
    public ApiResponse<Void> withdraw(
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        settingCommandService.withdraw(principal.getUserId());
        return ApiResponse.onSuccess(SettingSuccessCode.USER_WITHDRAW_SUCCESS);
    }

}
