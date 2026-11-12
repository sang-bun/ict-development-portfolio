package com.capstone.fertility.domain.home.controller;

import com.capstone.fertility.domain.home.dto.res.HomeResDTO;
import com.capstone.fertility.domain.home.service.HomeQueryService;
import com.capstone.fertility.global.apiPayLoad.ApiResponse;
import com.capstone.fertility.global.apiPayLoad.code.GeneralSuccessCode;
import com.capstone.fertility.global.security.CustomPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/home")
public class HomeController {

    private final HomeQueryService homeQueryService;

    @GetMapping
    @Operation(summary = "홈 화면 조회", description = """
            로그인 사용자 홈 데이터.
            - user: nickname, level, exp, dailyRewardCapReached, flowerType(Lv.5+ 최근 꽃)
            - recentTest: 최신 검사 1건(score 0~100, riskLevel SAFE|WARNING|DANGER, topFactors 가변). 없으면 null
            - todayMissions: GET /api/missions/today 와 동일(진행 중 미션 최대 3)
            - actions: TEST(검사하기), GUIDE(검사 상세 리포트, recentTest 있을 때만)
            - unreadNotiCount: 알림 미구현 시 0
            """)
    public ApiResponse<HomeResDTO.HomeDTO> getHome(
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        HomeResDTO.HomeDTO home = homeQueryService.getHome(principal.getUserId());
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, home);
    }
}
