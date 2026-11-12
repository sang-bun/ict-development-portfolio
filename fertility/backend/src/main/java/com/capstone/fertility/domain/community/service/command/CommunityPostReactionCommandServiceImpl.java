package com.capstone.fertility.domain.community.service.command;

import com.capstone.fertility.domain.community.dto.res.CommunityResDTO;
import com.capstone.fertility.domain.community.entity.Post;
import com.capstone.fertility.domain.community.entity.PostBookmark;
import com.capstone.fertility.domain.community.entity.PostLike;
import com.capstone.fertility.domain.community.repository.CommunityPostBookmarkRepository;
import com.capstone.fertility.domain.community.repository.CommunityPostLikeRepository;
import com.capstone.fertility.domain.community.support.CommunityAccessSupport;
import com.capstone.fertility.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommunityPostReactionCommandServiceImpl implements CommunityPostReactionCommandService {

    private final CommunityPostLikeRepository postLikeRepository;
    private final CommunityPostBookmarkRepository postBookmarkRepository;
    private final CommunityAccessSupport accessSupport;

    @Override
    public CommunityResDTO.LikeToggle toggleLike(Long userId, Long postId) {
        Post post = accessSupport.requireActivePost(postId);
        User user = accessSupport.requireUser(userId);
        boolean liked;
        var existing = postLikeRepository.findByPost_IdAndUser_Id(postId, userId);
        if (existing.isPresent()) {
            postLikeRepository.delete(existing.get());
            post.adjustLikeCount(-1);
            liked = false;
        } else {
            postLikeRepository.save(PostLike.builder().post(post).user(user).build());
            post.adjustLikeCount(1);
            liked = true;
        }
        return CommunityResDTO.LikeToggle.builder()
                .liked(liked)
                .likeCount(post.getLikeCount())
                .build();
    }

    @Override
    public CommunityResDTO.BookmarkToggle toggleBookmark(Long userId, Long postId) {
        Post post = accessSupport.requireActivePost(postId);
        User user = accessSupport.requireUser(userId);
        boolean bookmarked;
        var existing = postBookmarkRepository.findByPost_IdAndUser_Id(postId, userId);
        if (existing.isPresent()) {
            postBookmarkRepository.delete(existing.get());
            bookmarked = false;
        } else {
            postBookmarkRepository.save(PostBookmark.builder().post(post).user(user).build());
            bookmarked = true;
        }
        return CommunityResDTO.BookmarkToggle.builder()
                .bookmarked(bookmarked)
                .build();
    }
}
