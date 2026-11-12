package umc.fitty.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import umc.fitty.service.NotificationService;
import umc.fitty.web.dto.NotificationDTO.NotificationPatchRequestDTO;
import umc.fitty.web.dto.NotificationDTO.NotificationResponseDTO;
import umc.fitty.config.security.SecurityUtil;

@RestController
@RequestMapping("/users/me/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @Operation(
        summary = "내 알림 설정 조회",
        security = { @SecurityRequirement(name = "bearerAuth") }
    )
    public ResponseEntity<NotificationResponseDTO> getMySettings() {
        Long userId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(notificationService.getMySettings(userId));
    }

    @PatchMapping
    @Operation(
        summary = "내 알림 설정 수정 (부분 업데이트)",
        description = "전송한 필드만 수정합니다.",
        security = { @SecurityRequirement(name = "bearerAuth") }
    )
    public ResponseEntity<Void> patchMySettings(
            @RequestBody @Valid NotificationPatchRequestDTO request
    ) {
        Long userId = SecurityUtil.getCurrentUserId();
        notificationService.patchMySettings(userId, request);
        return ResponseEntity.noContent().build();
    }
}
