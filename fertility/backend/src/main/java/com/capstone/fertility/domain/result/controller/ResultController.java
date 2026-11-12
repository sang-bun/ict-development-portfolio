package com.capstone.fertility.domain.result.controller;

import com.capstone.fertility.domain.result.dto.res.ResultResDTO;
import com.capstone.fertility.domain.result.exception.code.ResultSuccessCode;
import com.capstone.fertility.domain.result.service.query.ResultQueryService;
import com.capstone.fertility.global.apiPayLoad.ApiResponse;
import com.capstone.fertility.global.security.CustomPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/results")
public class ResultController {

    private final ResultQueryService resultQueryService;

    @GetMapping("/history")
    @Operation(
            summary = "과거 검사 결과 이력(월별)",
            description = "쿼리 없음: year=2026, month=이번 달. year·month를 함께 지정하면 해당 연·월의 결과만 조회합니다. 본인 데이터만 반환합니다."
    )
    public ApiResponse<ResultResDTO.ResultHistoryDTO> getHistory(
            @AuthenticationPrincipal CustomPrincipal principal,
            @Parameter(description = "연도 (생략 시 2026)")
            @RequestParam(required = false) Integer year,
            @Parameter(description = "월 1~12 (생략 시 이번 달)")
            @RequestParam(required = false) Integer month
    ) {
        ResultResDTO.ResultHistoryDTO result = resultQueryService.getHistory(principal.getUserId(), year, month);
        return ApiResponse.onSuccess(ResultSuccessCode.RESULT_HISTORY_FETCHED, result);
    }
}
