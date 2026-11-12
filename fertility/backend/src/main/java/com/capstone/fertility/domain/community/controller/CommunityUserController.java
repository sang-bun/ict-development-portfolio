package com.capstone.fertility.domain.community.controller;

import com.capstone.fertility.domain.community.exception.code.CommunitySuccessCode;
import com.capstone.fertility.domain.community.service.command.CommunityModerationCommandService;
import com.capstone.fertility.global.apiPayLoad.ApiResponse;
import com.capstone.fertility.global.security.CustomPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/community/users")
public class CommunityUserController {

    private final CommunityModerationCommandService moderationCommandService;

    @PostMapping("/{userId}/block")
    @Operation(summary = "사용자 차단")
    public ApiResponse<Void> blockUser(
            @AuthenticationPrincipal CustomPrincipal principal,
            @PathVariable Long userId
    ) {
        moderationCommandService.blockUser(principal.getUserId(), userId);
        return ApiResponse.onSuccess(CommunitySuccessCode.USER_BLOCKED);
    }
}
