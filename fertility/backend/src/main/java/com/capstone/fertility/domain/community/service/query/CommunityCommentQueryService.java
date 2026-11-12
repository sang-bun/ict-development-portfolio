package com.capstone.fertility.domain.community.service.query;

import com.capstone.fertility.domain.community.dto.res.CommunityResDTO;

public interface CommunityCommentQueryService {

    CommunityResDTO.CommentPage listComments(Long viewerUserId, Long postId, int page, int size);
}
