package com.example.givy.domain.guide.controller;

import com.example.givy.domain.guide.code.GuideSuccessCode;
import com.example.givy.domain.guide.dto.res.GuideResDTO;
import com.example.givy.domain.guide.service.command.GuideCommandService;
import com.example.givy.domain.guide.service.query.GuideQueryService;
import com.example.givy.global.apiPayLoad.ApiResponse;
import com.example.givy.global.security.CustomPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/guide")
public class GuideController implements GuideControllerDocs {

    private final GuideCommandService guideCommandService;
    private final GuideQueryService guideQueryService;

    /* 04-01, 04-06 가이드 목록 조회 API (통합) */
    @GetMapping()
    public ApiResponse<Slice<GuideResDTO.GuideSummaryDTO>> getGuides(
            @RequestParam(required = false) String category,
            @PageableDefault(page = 0, size = 10)
            Pageable pageable
    ) {
        return ApiResponse.onSuccess(
                GuideSuccessCode.GUIDE_LIST_FETCHED,
                guideQueryService.getGuides(category, pageable)
        );
    }


    /* 04-02 가이드 상세 조회 API */
    @GetMapping("/{guideId}")
    public ApiResponse<GuideResDTO.GuideDetailDTO> getGuideDetail(
            @AuthenticationPrincipal CustomPrincipal principal,
            @PathVariable Long guideId
    ) {
        return ApiResponse.onSuccess(
                GuideSuccessCode.GUIDE_DETAIL_FETCHED,
                guideQueryService.getGuideDetail(
                        guideId,
                        principal.getUserId()
                )
        );
    }

    /* 04-03 가이드 단건 좋아요 API */
    @PostMapping("/{guideId}/guide_like")
    public ApiResponse<Void> likeGuide(
            @AuthenticationPrincipal CustomPrincipal principal,
            @PathVariable Long guideId
    ){
        guideCommandService.toggleLike(guideId, principal.getUserId());
        return ApiResponse.onSuccess(GuideSuccessCode.GUIDE_LIKED);
    }

    /* 04-04 가이드 단건 저장 API */
    @PostMapping("/{guideId}/guide_store")
    public ApiResponse<Void> storeGuide(
            @AuthenticationPrincipal CustomPrincipal principal,
            @PathVariable Long guideId
    ){
        guideCommandService.toggleStore(guideId, principal.getUserId());
        return ApiResponse.onSuccess(GuideSuccessCode.GUIDE_STORED);
    }


    /* 04-05 내가 저장한 가이드 조회 API*/
    @GetMapping("/users/me/store")
    public ApiResponse<List<GuideResDTO.GuideSummaryDTO>> getMyStoredGuides(
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        return ApiResponse.onSuccess(
                GuideSuccessCode.GUIDE_MY_STORED_LIST_FETCHED,
                guideQueryService.getMyStoredGuides(principal.getUserId())
        );
    }

}
