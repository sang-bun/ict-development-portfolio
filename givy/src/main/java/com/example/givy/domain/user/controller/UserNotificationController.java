package com.example.givy.domain.user.controller;

import com.example.givy.domain.user.code.UserNotificationSuccessCode;
import com.example.givy.domain.user.dto.res.UserNotificationResDTO;
import com.example.givy.domain.user.service.command.notification.UserNotificationCommandService;
import com.example.givy.domain.user.service.query.UserNotificationQueryService;
import com.example.givy.global.apiPayLoad.ApiResponse;
import com.example.givy.global.security.CustomPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/userNotifications")
public class UserNotificationController implements UserNotificationControllerDocs {
    private final UserNotificationQueryService userNotificationQueryService;
    private final UserNotificationCommandService userNotificationCommandService;

    /* 07-01 사용자 알림 조회 */
    @GetMapping()
    public ApiResponse<UserNotificationResDTO.UserNotificationInfoListDTO> getUserNotifications(
            @AuthenticationPrincipal CustomPrincipal principal,
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "20", required = false) int size,
            @RequestParam(required = false) Boolean isRead
    ){
        return ApiResponse.onSuccess(UserNotificationSuccessCode.USER_NOTIFICATION_LIST_FOUND, userNotificationQueryService.getUserNotifications(principal.getUserId(), page, size, isRead));
    }

    /* 07-02 사용자 알림 읽음 처리 */
    @PatchMapping("/{userNotificationId}")
    public ApiResponse<UserNotificationResDTO.ReadUserNotification> readNotification(
            @AuthenticationPrincipal CustomPrincipal principal,
            @PathVariable Long userNotificationId
    ){
        UserNotificationResDTO.ReadUserNotification notification = userNotificationCommandService.readNotification(principal.getUserId(), userNotificationId);

        if(notification == null){
            return ApiResponse.onSuccess(UserNotificationSuccessCode.USER_NOTIFICATION_ALREADY_READ, null);
        }

        return ApiResponse.onSuccess(UserNotificationSuccessCode.USER_NOTIFICATION_MARKED_AS_READ, notification);
    }
}
