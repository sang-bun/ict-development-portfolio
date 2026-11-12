package com.capstone.fertility.domain.community.service.command;

import com.capstone.fertility.domain.community.dto.req.CommunityReqDTO;
import com.capstone.fertility.domain.community.dto.res.CommunityResDTO;

public interface CommunityPostCommandService {

    CommunityResDTO.PostDetail createPost(Long userId, CommunityReqDTO.PostWrite request);

    CommunityResDTO.PostDetail updatePost(Long userId, Long postId, CommunityReqDTO.PostWrite request);

    void deletePost(Long userId, Long postId);
}
