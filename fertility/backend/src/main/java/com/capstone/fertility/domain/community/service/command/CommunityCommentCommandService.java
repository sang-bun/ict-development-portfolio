package com.capstone.fertility.domain.community.service.command;

import com.capstone.fertility.domain.community.dto.req.CommunityReqDTO;
import com.capstone.fertility.domain.community.dto.res.CommunityResDTO;

public interface CommunityCommentCommandService {

    CommunityResDTO.CommentItem createComment(Long userId, Long postId, CommunityReqDTO.CommentWrite request);

    CommunityResDTO.CommentItem updateComment(Long userId, Long commentId, CommunityReqDTO.CommentWrite request);

    void deleteComment(Long userId, Long commentId);

    CommunityResDTO.LikeToggle toggleCommentLike(Long userId, Long commentId);
}
