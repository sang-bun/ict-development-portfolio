package com.capstone.fertility.domain.community.service.command;

import com.capstone.fertility.domain.community.dto.req.CommunityReqDTO;

public interface CommunityModerationCommandService {

    void reportPost(Long reporterId, Long postId, CommunityReqDTO.PostReport request);

    void blockUser(Long blockerId, Long blockedUserId);
}
