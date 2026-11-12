package com.example.givy.domain.guide.controller;

import com.example.givy.domain.guide.dto.res.GuideResDTO;
import com.example.givy.global.apiPayLoad.ApiResponse;
import com.example.givy.global.security.CustomPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "04 Guide", description = "04번대 가이드 API")
public interface GuideControllerDocs {

    /* 04-01, 04-06 가이드 목록 조회 */
    @Operation(
            summary = "04-01 / 04-06 가이드 목록 조회",
            operationId = "04-01-06",
            description = "카테고리 선택형 가이드 목록을 페이징(Slice)으로 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "가이드 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = GuideResDTO.GuideSummaryDTO.class))
            )
    })
    @GetMapping
    ApiResponse<Slice<GuideResDTO.GuideSummaryDTO>> getGuides(
            @Parameter(description = "가이드 카테고리 (ETF, STOCK 등)", required = false)
            @RequestParam(required = false) String category,

            Pageable pageable
    );

    /* 04-02 가이드 상세 조회 */
    @Operation(
            summary = "04-02 가이드 상세 조회",
            operationId = "04-02",
            description = "가이드 상세 정보 및 좋아요/저장 여부를 조회합니다."
    )
    @GetMapping("/{guideId}")
    ApiResponse<GuideResDTO.GuideDetailDTO> getGuideDetail(
            @AuthenticationPrincipal CustomPrincipal principal,
            @PathVariable Long guideId
    );

    /* 04-03 가이드 좋아요 */
    @Operation(
            summary = "04-03 가이드 좋아요",
            operationId = "04-03",
            description = "가이드 좋아요를 토글합니다."
    )
    @PostMapping("/{guideId}/guide_like")
    ApiResponse<Void> likeGuide(
            @AuthenticationPrincipal CustomPrincipal principal,
            @PathVariable Long guideId
    );

    /* 04-04 가이드 저장 */
    @Operation(
            summary = "04-04 가이드 저장",
            operationId = "04-04",
            description = "가이드 저장을 토글합니다."
    )
    @PostMapping("/{guideId}/guide_store")
    ApiResponse<Void> storeGuide(
            @AuthenticationPrincipal CustomPrincipal principal,
            @PathVariable Long guideId
    );

    /* 04-05 내가 저장한 가이드 조회 */
    @Operation(
            summary = "04-05 내가 저장한 가이드 조회",
            operationId = "04-05",
            description = "내가 저장한 가이드를 최신순으로 조회합니다."
    )
    @GetMapping("/users/me/store")
    ApiResponse<?> getMyStoredGuides(
            @AuthenticationPrincipal CustomPrincipal principal
    );
}
