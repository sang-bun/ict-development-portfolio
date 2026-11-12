package com.capstone.fertility.domain.test.controller;

import com.capstone.fertility.domain.test.dto.req.TestReqDTO;
import com.capstone.fertility.domain.test.dto.res.TestResDTO;
import com.capstone.fertility.domain.test.exception.code.TestSuccessCode;
import com.capstone.fertility.domain.test.service.command.TestCommandService;
import com.capstone.fertility.domain.result.service.command.TestResultCommandService;
import com.capstone.fertility.domain.test.service.query.TestQueryService;
import com.capstone.fertility.global.apiPayLoad.ApiResponse;
import com.capstone.fertility.global.apiPayLoad.code.GeneralSuccessCode;
import com.capstone.fertility.global.security.CustomPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tests")
public class TestController {

    private final TestCommandService testCommandService;
    private final TestQueryService testQueryService;
    private final TestResultCommandService testResultCommandService;

    @PostMapping("/start")
    @Operation(summary = "검사 세션 시작", description = "0단계(성별 선택) 후 검사 세션을 생성하고 sessionId를 반환합니다.")
    public ApiResponse<TestResDTO.CreateSessionResDTO> startSession(
            @AuthenticationPrincipal CustomPrincipal principal,
            @Valid @RequestBody TestReqDTO.Start request
    ) {
        TestResDTO.CreateSessionResDTO result = testCommandService.start(principal.getUserId(), request);
        return ApiResponse.onSuccess(TestSuccessCode.TEST_SESSION_CREATED, result);
    }

    @GetMapping("/{sessionId}")
    @Operation(summary = "검사 세션 조회(임시 저장 복구)", description = "저장된 세션의 currentStep과 입력값을 반환합니다. 재진입 시 폼 복구용이며, 본인 세션만 조회 가능합니다.")
    public ApiResponse<TestResDTO.SessionDetailDTO> getSession(
            @AuthenticationPrincipal CustomPrincipal principal,
            @PathVariable Long sessionId
    ) {
        TestResDTO.SessionDetailDTO result = testQueryService.getSession(principal.getUserId(), sessionId);
        return ApiResponse.onSuccess(TestSuccessCode.TEST_SESSION_FETCHED, result);
    }

    @GetMapping("/{sessionId}/interim-report")
    @Operation(
            summary = "중간 보고서 조회",
            description = "사용자 입력(수면은 시·분 둘 다 있어야 수면 비교 계산, 키/몸무게/나이/성별 등)을 기반으로 평균 대비 차이·BMI·유병률·(제출 후) AI 점수·topFactors 등을 반환합니다."
    )
    public ApiResponse<TestResDTO.InterimReportDTO> getInterimReport(
            @AuthenticationPrincipal CustomPrincipal principal,
            @PathVariable Long sessionId
    ) {
        TestResDTO.InterimReportDTO result = testQueryService.getInterimReport(principal.getUserId(), sessionId);
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, result);
    }

    @PatchMapping("/{sessionId}/step/male")
    @Operation(
            summary = "남성 임시 저장",
            description = "남성 전용 질문(step 1~11) 임시 저장. 수면을 보낼 때는 sleepHours와 sleepMinutes를 **함께** 보내야 하며(한쪽만 내면 TEST400_7), 분 0~59·합계 24시간 이하입니다. 둘 다 생략하면 수면 필드는 갱신하지 않습니다."
    )
    public ApiResponse<Void> saveMaleStep(
            @AuthenticationPrincipal CustomPrincipal principal,
            @PathVariable Long sessionId,
            @Valid @RequestBody TestReqDTO.MaleStepSave request
    ) {
        testCommandService.saveMaleStep(principal.getUserId(), sessionId, request);
        return ApiResponse.onSuccess(TestSuccessCode.TEST_STEP_SAVED, null);
    }

    @PatchMapping("/{sessionId}/step/female")
    @Operation(
            summary = "여성 임시 저장",
            description = "여성 전용 질문(step 1~9) 임시 저장. smokeLevel/binge12는 0~2 tier 또는 설문 원시값(개비·연간 폭음 일수) 가능. "
                    + "cigarettesPerDay/bingeDaysPerYear 명시 권장. 음주: drinkStatus(한글·NEVER 등) 또는 drinkLevel(0~2). "
                    + "수면은 sleepHours+sleepMinutes 쌍 검증(TEST400_7) 및 합계 ≤24h."
    )
    public ApiResponse<Void> saveFemaleStep(
            @AuthenticationPrincipal CustomPrincipal principal,
            @PathVariable Long sessionId,
            @Valid @RequestBody TestReqDTO.FemaleStepSave request
    ) {
        testCommandService.saveFemaleStep(principal.getUserId(), sessionId, request);
        return ApiResponse.onSuccess(TestSuccessCode.TEST_STEP_SAVED, null);
    }

    @PostMapping("/{sessionId}/submit")
    @Operation(
            summary = "최종 제출 및 AI 예측 실행",
            description = "PSS 스트레스 10문항 제출 후 AI 예측을 실행하고 결과를 저장합니다. " +
                    "세션에 수면 시·분이 모두 저장되어 있어야 하며(없거나 한쪽만 있으면 TEST400_7), 임시 저장 시와 동일한 유효 범위를 만족해야 합니다. " +
                    "본인 세션만 제출 가능하며, 이미 결과가 있으면 재제출 불가. " +
                    "응답의 topFactors는 활성 위험요인 전체 목록(가변 길이, 0~N개)이며 " +
                    "Top 3 고정이 아닙니다. (구 스펙의 top1/2/3, mission_candidates 필드는 제거됨)"
    )
    public ApiResponse<TestResDTO.SubmitResult> submitResults(
            @AuthenticationPrincipal CustomPrincipal principal,
            @PathVariable Long sessionId,
            @Valid @RequestBody TestReqDTO.Submit request
    ) {
        TestResDTO.SubmitResult result = testResultCommandService.submit(principal.getUserId(), sessionId, request);
        return ApiResponse.onSuccess(TestSuccessCode.TEST_SUBMITTED, result);
    }
}
