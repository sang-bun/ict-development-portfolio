package com.example.givy.domain.settings.controller;

import com.example.givy.domain.settings.dto.req.SettingReqDTO;
import com.example.givy.domain.settings.dto.res.SettingResDTO;
import com.example.givy.global.apiPayLoad.ApiResponse;
import com.example.givy.global.security.CustomPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "05 Settings", description = "05번대 설정 API")
public interface SettingControllerDocs {

    /* 05-01 프로필 수정 */
    @Operation(
            summary = "05-01 프로필 수정",
            operationId = "05-01",
            description = "닉네임, 앱 언어, 프로필 이미지를 수정합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "프로필 수정 성공"
            )
    })
    @PatchMapping("/users/profile")
    ApiResponse<Void> updateProfile(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomPrincipal principal,

            @RequestBody
            @Valid
            @Schema(implementation = SettingReqDTO.UpdateProfileDTO.class)
            SettingReqDTO.UpdateProfileDTO request
    );

    /* 05-02 비밀번호 변경 */
    @Operation(
            summary = "05-02 비밀번호 변경",
            operationId = "05-02",
            description = "현재 비밀번호를 검증한 뒤 새 비밀번호로 변경합니다. (소셜 로그인 유저 제외)"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "비밀번호 변경 성공"
            )
    })
    @PatchMapping("/users/password")
    ApiResponse<Void> updatePassword(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomPrincipal principal,

            @RequestBody
            @Valid
            @Schema(implementation = SettingReqDTO.UpdatePasswordDTO.class)
            SettingReqDTO.UpdatePasswordDTO request
    );

    /* 05-03 투자 성향 재설정 */
    @Operation(
            summary = "05-03 투자 성향 재설정",
            operationId = "05-03",
            description = "해당 유저의 최신 투자 성향(Tendency)을 삭제합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "투자 성향 재설정 성공"
            )
    })
    @DeleteMapping("/tendency")
    ApiResponse<Void> resetTendency(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomPrincipal principal
    );

    /* 05-04 알림 설정 조회 */
    @Operation(
            summary = "05-04 알림 설정 조회",
            operationId = "05-04",
            description = "가이드/챌린지 알림 수신 설정을 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "알림 설정 조회 성공",
                    content = @Content(
                            schema = @Schema(
                                    implementation = SettingResDTO.NotificationSettingDTO.class
                            )
                    )
            )
    })
    @GetMapping("/notifications")
    ApiResponse<SettingResDTO.NotificationSettingDTO> getNotificationSetting(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomPrincipal principal
    );

    /* 05-05 알림 수신 설정 변경 */
    @Operation(
            summary = "05-05 알림 수신 설정 변경",
            operationId = "05-05",
            description = "알림 수신 여부를 변경합니다. (최초 요청 시 설정 정보가 생성됩니다)"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "알림 수신 설정 변경 성공"
            )
    })
    @PatchMapping("/notifications")
    ApiResponse<Void> updateNotificationSetting(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomPrincipal principal,

            @RequestBody
            @Valid
            @Schema(implementation = SettingReqDTO.UpdateNotificationDTO.class)
            SettingReqDTO.UpdateNotificationDTO request
    );

    /* 05-06 앱 언어 변경 */
    @Operation(
            summary = "05-06 앱 언어 변경",
            operationId = "05-06",
            description = "앱에서 사용하는 기본 언어를 변경합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "앱 언어 변경 성공"
            )
    })
    @PatchMapping("/users/language")
    ApiResponse<Void> updateLanguage(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomPrincipal principal,

            @RequestBody
            @Valid
            @Schema(implementation = SettingReqDTO.UpdateLanguageDTO.class)
            SettingReqDTO.UpdateLanguageDTO request
    );

    /* 05-08 회원 탈퇴 */
    @Operation(
            summary = "05-08 회원 탈퇴",
            operationId = "05-08",
            description = "회원 탈퇴를 진행합니다. (soft delete 방식)"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "회원 탈퇴 성공"
            )
    })
    @DeleteMapping("/users")
    ApiResponse<Void> withdraw(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomPrincipal principal
    );
}
