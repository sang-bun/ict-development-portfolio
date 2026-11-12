package com.capstone.fertility.domain.community.support;

import com.capstone.fertility.domain.community.entity.Post;
import com.capstone.fertility.domain.community.enums.PostStatus;
import com.capstone.fertility.domain.community.exception.CommunityException;
import com.capstone.fertility.domain.community.exception.code.CommunityErrorCode;
import com.capstone.fertility.domain.community.repository.CommunityPostRepository;
import com.capstone.fertility.domain.community.repository.CommunityUserBlockRepository;
import com.capstone.fertility.domain.user.entity.User;
import com.capstone.fertility.domain.user.exception.UserException;
import com.capstone.fertility.domain.user.exception.code.UserErrorCode;
import com.capstone.fertility.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class CommunityAccessSupport {

    private final CommunityPostRepository postRepository;
    private final CommunityUserBlockRepository userBlockRepository;
    private final UserRepository userRepository;

    public User requireUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_ID_NOT_FOUND));
    }

    public Post requireActivePost(Long postId) {
        return postRepository.findActiveByIdWithAuthor(postId, PostStatus.ACTIVE)
                .orElseThrow(() -> new CommunityException(CommunityErrorCode.POST_NOT_FOUND));
    }

    public Post requireOwnedActivePost(Long postId, Long userId) {
        Post post = requireActivePost(postId);
        if (!post.isAuthor(userId)) {
            throw new CommunityException(CommunityErrorCode.POST_FORBIDDEN);
        }
        return post;
    }

    public Set<Long> blockedAuthorIds(Long viewerUserId) {
        if (viewerUserId == null) {
            return Collections.emptySet();
        }
        return userBlockRepository.findBlockedUserIds(viewerUserId);
    }
}
