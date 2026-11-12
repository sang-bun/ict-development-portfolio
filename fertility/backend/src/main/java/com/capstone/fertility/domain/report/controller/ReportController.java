package com.capstone.fertility.domain.report.controller;

import com.capstone.fertility.domain.report.dto.res.ReportResDTO;
import com.capstone.fertility.domain.report.exception.code.ReportSuccessCode;
import com.capstone.fertility.domain.report.service.ReportService;
import com.capstone.fertility.global.apiPayLoad.ApiResponse;
import com.capstone.fertility.global.security.CustomPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/results")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/{resultId}")
    @Operation(
            summary = "상세 리포트 조회",
            description = "검사 결과 ID로 LLM 기반 맞춤형 상세 리포트를 생성합니다. 본인 결과만 조회 가능합니다."
    )
    public ApiResponse<ReportResDTO.DetailReport> getDetailReport(
            @AuthenticationPrincipal CustomPrincipal principal,
            @Parameter(description = "검사 결과 ID") @PathVariable Long resultId
    ) {
        ReportResDTO.DetailReport report = reportService.generateReport(principal.getUserId(), resultId);
        return ApiResponse.onSuccess(ReportSuccessCode.REPORT_GENERATED, report);
    }
}
