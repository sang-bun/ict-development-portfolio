package com.example.givy.domain.user.controller;

import com.example.givy.domain.user.dto.res.UserNotificationResDTO;
import com.example.givy.global.apiPayLoad.ApiResponse;
import com.example.givy.global.security.CustomPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "07 UserNotification", description = "07번대 사용자 알림 API")
public interface UserNotificationControllerDocs {

    /* 07-01 사용자 알림 조회 */
    @Operation(
            summary = "07-01 사용자 알림 조회",
            operationId = "07-01",
            description = "사용자에게 배정된 알림을 읽음, 안 읽음, 전체를 기준으로 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "사용자 알림 조회 성공 (USER_NOTIFICATION200_1)",
                    content = @Content(schema = @Schema(implementation = UserNotificationResDTO.UserNotificationInfoListDTO.class))
            )
    })
    @GetMapping()
    ApiResponse<UserNotificationResDTO.UserNotificationInfoListDTO> getUserNotifications(
            @AuthenticationPrincipal CustomPrincipal principal,
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "20", required = false) int size,
            @RequestParam(required = false) Boolean isRead
    );


    /* 07-02 사용자 알림 읽음 처리 */
    @Operation(
            summary = "07-02 사용자 알림 읽음 처리",
            operationId = "07-02",
            description = "주어진 사용자 알림을 읽음 처리합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "사용자 알림 읽음 처리 성공 (USER_NOTIFICATION200_2)",
                    content = @Content(schema = @Schema(implementation = UserNotificationResDTO.ReadUserNotification.class))
            )
    })
    @PatchMapping("/{userNotificationId}")
    ApiResponse<UserNotificationResDTO.ReadUserNotification> readNotification(
            @AuthenticationPrincipal CustomPrincipal principal,
            @PathVariable Long userNotificationId
    );
}
