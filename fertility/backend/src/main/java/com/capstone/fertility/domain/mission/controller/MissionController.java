package com.capstone.fertility.domain.mission.controller;

import com.capstone.fertility.domain.mission.exception.code.MissionSuccessCode;
import com.capstone.fertility.domain.mission.service.command.SproutCycleCommandService;
import com.capstone.fertility.global.apiPayLoad.ApiResponse;
import com.capstone.fertility.global.security.CustomPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/missions")
public class MissionController {

    private final SproutCycleCommandService sproutCycleCommandService;

    @PostMapping("/sprout/reset-after-retest")
    @Operation(
            summary = "재검사 후 새싹 사이클 초기화",
            description = "명세: Lv.5(꽃 달성) 상태에서 AI 건강 재검사를 마친 뒤 호출합니다. 꽃 도감은 유지하고 새싹 레벨·EXP만 Lv.1·0으로 초기화합니다."
    )
    public ApiResponse<String> resetSproutAfterRetest(
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        sproutCycleCommandService.resetAfterRetest(principal.getUserId());
        return ApiResponse.onSuccess(MissionSuccessCode.SPROUT_CYCLE_RESET, "OK");
    }
}
