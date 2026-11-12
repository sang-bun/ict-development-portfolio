package com.capstone.fertility.domain.community.service.query;

import com.capstone.fertility.domain.community.dto.res.CommunityResDTO;
import com.capstone.fertility.domain.community.enums.PostCategory;
import com.capstone.fertility.domain.community.enums.PostSortType;

public interface CommunityPostQueryService {

    CommunityResDTO.PostPage listPosts(
            Long viewerUserId,
            String category,
            String sort,
            String query,
            boolean bookmarkedOnly,
            int page,
            int size
    );

    CommunityResDTO.PostDetail getPost(Long viewerUserId, Long postId);

    CommunityResDTO.PostDetail getPostWithoutViewCount(Long viewerUserId, Long postId);
}
