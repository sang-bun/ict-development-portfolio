package com.capstone.fertility.domain.community.controller;

import com.capstone.fertility.domain.community.dto.req.CommunityReqDTO;
import com.capstone.fertility.domain.community.dto.res.CommunityResDTO;
import com.capstone.fertility.domain.community.exception.code.CommunitySuccessCode;
import com.capstone.fertility.domain.community.service.command.CommunityModerationCommandService;
import com.capstone.fertility.domain.community.service.command.CommunityPostCommandService;
import com.capstone.fertility.domain.community.service.command.CommunityPostReactionCommandService;
import com.capstone.fertility.domain.community.service.query.CommunityPostQueryService;
import com.capstone.fertility.global.apiPayLoad.ApiResponse;
import com.capstone.fertility.global.security.CustomPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/community/posts")
public class CommunityPostController {

    private final CommunityPostQueryService postQueryService;
    private final CommunityPostCommandService postCommandService;
    private final CommunityPostReactionCommandService reactionCommandService;
    private final CommunityModerationCommandService moderationCommandService;

    @GetMapping
    @Operation(summary = "게시글 목록")
    public ApiResponse<CommunityResDTO.PostPage> listPosts(
            @AuthenticationPrincipal CustomPrincipal principal,
            @RequestParam(required = false) String category,
            @RequestParam(required = false, defaultValue = "latest") String sort,
            @RequestParam(required = false) String q,
            @RequestParam(required = false, defaultValue = "false") boolean bookmarkedOnly,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size
    ) {
        CommunityResDTO.PostPage result = postQueryService.listPosts(
                principal.getUserId(), category, sort, q, bookmarkedOnly, page, size
        );
        return ApiResponse.onSuccess(CommunitySuccessCode.POST_LIST_FETCHED, result);
    }

    @GetMapping("/{postId}")
    @Operation(summary = "게시글 상세")
    public ApiResponse<CommunityResDTO.PostDetail> getPost(
            @AuthenticationPrincipal CustomPrincipal principal,
            @PathVariable Long postId
    ) {
        CommunityResDTO.PostDetail result = postQueryService.getPost(principal.getUserId(), postId);
        return ApiResponse.onSuccess(CommunitySuccessCode.POST_DETAIL_FETCHED, result);
    }

    @PostMapping
    @Operation(summary = "게시글 작성")
    public ApiResponse<CommunityResDTO.PostDetail> createPost(
            @AuthenticationPrincipal CustomPrincipal principal,
            @RequestBody CommunityReqDTO.PostWrite request
    ) {
        CommunityResDTO.PostDetail result = postCommandService.createPost(principal.getUserId(), request);
        return ApiResponse.onSuccess(CommunitySuccessCode.POST_CREATED, result);
    }

    @PutMapping("/{postId}")
    @Operation(summary = "게시글 수정")
    public ApiResponse<CommunityResDTO.PostDetail> updatePost(
            @AuthenticationPrincipal CustomPrincipal principal,
            @PathVariable Long postId,
            @RequestBody CommunityReqDTO.PostWrite request
    ) {
        CommunityResDTO.PostDetail result = postCommandService.updatePost(principal.getUserId(), postId, request);
        return ApiResponse.onSuccess(CommunitySuccessCode.POST_UPDATED, result);
    }

    @DeleteMapping("/{postId}")
    @Operation(summary = "게시글 삭제")
    public ApiResponse<Void> deletePost(
            @AuthenticationPrincipal CustomPrincipal principal,
            @PathVariable Long postId
    ) {
        postCommandService.deletePost(principal.getUserId(), postId);
        return ApiResponse.onSuccess(CommunitySuccessCode.POST_DELETED);
    }

    @PostMapping("/{postId}/like")
    @Operation(summary = "게시글 좋아요 토글")
    public ApiResponse<CommunityResDTO.LikeToggle> toggleLike(
            @AuthenticationPrincipal CustomPrincipal principal,
            @PathVariable Long postId
    ) {
        CommunityResDTO.LikeToggle result = reactionCommandService.toggleLike(principal.getUserId(), postId);
        return ApiResponse.onSuccess(CommunitySuccessCode.POST_LIKE_TOGGLED, result);
    }

    @PostMapping("/{postId}/bookmark")
    @Operation(summary = "게시글 북마크 토글")
    public ApiResponse<CommunityResDTO.BookmarkToggle> toggleBookmark(
            @AuthenticationPrincipal CustomPrincipal principal,
            @PathVariable Long postId
    ) {
        CommunityResDTO.BookmarkToggle result = reactionCommandService.toggleBookmark(principal.getUserId(), postId);
        return ApiResponse.onSuccess(CommunitySuccessCode.POST_BOOKMARK_TOGGLED, result);
    }

    @PostMapping("/{postId}/report")
    @Operation(summary = "게시글 신고")
    public ApiResponse<Void> reportPost(
            @AuthenticationPrincipal CustomPrincipal principal,
            @PathVariable Long postId,
            @RequestBody CommunityReqDTO.PostReport request
    ) {
        moderationCommandService.reportPost(principal.getUserId(), postId, request);
        return ApiResponse.onSuccess(CommunitySuccessCode.POST_REPORTED);
    }
}
