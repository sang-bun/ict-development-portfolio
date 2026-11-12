package com.capstone.fertility.domain.community.controller;

import com.capstone.fertility.domain.community.dto.req.CommunityReqDTO;
import com.capstone.fertility.domain.community.dto.res.CommunityResDTO;
import com.capstone.fertility.domain.community.exception.code.CommunitySuccessCode;
import com.capstone.fertility.domain.community.service.command.CommunityCommentCommandService;
import com.capstone.fertility.domain.community.service.query.CommunityCommentQueryService;
import com.capstone.fertility.global.apiPayLoad.ApiResponse;
import com.capstone.fertility.global.security.CustomPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/community")
public class CommunityCommentController {

    private final CommunityCommentQueryService commentQueryService;
    private final CommunityCommentCommandService commentCommandService;

    @GetMapping("/posts/{postId}/comments")
    @Operation(summary = "댓글 목록")
    public ApiResponse<CommunityResDTO.CommentPage> listComments(
            @AuthenticationPrincipal CustomPrincipal principal,
            @PathVariable Long postId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size
    ) {
        CommunityResDTO.CommentPage result = commentQueryService.listComments(principal.getUserId(), postId, page, size);
        return ApiResponse.onSuccess(CommunitySuccessCode.COMMENT_LIST_FETCHED, result);
    }

    @PostMapping("/posts/{postId}/comments")
    @Operation(summary = "댓글 작성")
    public ApiResponse<CommunityResDTO.CommentItem> createComment(
            @AuthenticationPrincipal CustomPrincipal principal,
            @PathVariable Long postId,
            @RequestBody CommunityReqDTO.CommentWrite request
    ) {
        CommunityResDTO.CommentItem result = commentCommandService.createComment(principal.getUserId(), postId, request);
        return ApiResponse.onSuccess(CommunitySuccessCode.COMMENT_CREATED, result);
    }

    @PutMapping("/comments/{commentId}")
    @Operation(summary = "댓글 수정")
    public ApiResponse<CommunityResDTO.CommentItem> updateComment(
            @AuthenticationPrincipal CustomPrincipal principal,
            @PathVariable Long commentId,
            @RequestBody CommunityReqDTO.CommentWrite request
    ) {
        CommunityResDTO.CommentItem result = commentCommandService.updateComment(principal.getUserId(), commentId, request);
        return ApiResponse.onSuccess(CommunitySuccessCode.COMMENT_UPDATED, result);
    }

    @DeleteMapping("/comments/{commentId}")
    @Operation(summary = "댓글 삭제")
    public ApiResponse<Void> deleteComment(
            @AuthenticationPrincipal CustomPrincipal principal,
            @PathVariable Long commentId
    ) {
        commentCommandService.deleteComment(principal.getUserId(), commentId);
        return ApiResponse.onSuccess(CommunitySuccessCode.COMMENT_DELETED);
    }

    @PostMapping("/comments/{commentId}/like")
    @Operation(summary = "댓글 좋아요 토글")
    public ApiResponse<CommunityResDTO.LikeToggle> toggleCommentLike(
            @AuthenticationPrincipal CustomPrincipal principal,
            @PathVariable Long commentId
    ) {
        CommunityResDTO.LikeToggle result = commentCommandService.toggleCommentLike(principal.getUserId(), commentId);
        return ApiResponse.onSuccess(CommunitySuccessCode.COMMENT_LIKE_TOGGLED, result);
    }
}
