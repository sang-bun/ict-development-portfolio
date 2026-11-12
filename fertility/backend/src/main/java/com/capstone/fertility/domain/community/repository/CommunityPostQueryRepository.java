package com.capstone.fertility.domain.community.repository;

import com.capstone.fertility.domain.community.entity.Post;
import com.capstone.fertility.domain.community.enums.PostCategory;
import com.capstone.fertility.domain.community.enums.PostSortType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface CommunityPostQueryRepository {

    Page<Post> searchPosts(
            Long viewerUserId,
            PostCategory category,
            PostSortType sort,
            String query,
            boolean bookmarkedOnly,
            Set<Long> blockedAuthorIds,
            Pageable pageable
    );
}
