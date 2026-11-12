package com.capstone.fertility.domain.community.service.command;

import com.capstone.fertility.domain.community.dto.res.CommunityResDTO;

public interface CommunityPostReactionCommandService {

    CommunityResDTO.LikeToggle toggleLike(Long userId, Long postId);

    CommunityResDTO.BookmarkToggle toggleBookmark(Long userId, Long postId);
}
