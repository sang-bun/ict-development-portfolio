package com.capstone.fertility.domain.community.support;

import com.capstone.fertility.domain.community.dto.res.CommunityResDTO;
import com.capstone.fertility.domain.community.entity.Comment;
import com.capstone.fertility.domain.community.entity.Post;
import com.capstone.fertility.domain.community.entity.PostImage;
import com.capstone.fertility.domain.user.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class CommunityMapper {

    public CommunityResDTO.Author toAuthor(User user) {
        return CommunityResDTO.Author.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }

    public CommunityResDTO.PostSummary toSummary(
            Post post,
            List<PostImage> images,
            boolean likedByMe,
            boolean bookmarkedByMe,
            Long viewerUserId
    ) {
        String thumbnail = images.isEmpty() ? null : images.get(0).getImageUrl();
        return CommunityResDTO.PostSummary.builder()
                .postId(post.getId())
                .category(post.getCategory())
                .title(post.getTitle())
                .bodyPreview(CommunityValidationSupport.bodyPreview(post.getBody()))
                .tags(post.getTags())
                .author(toAuthor(post.getAuthor()))
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .viewCount(post.getViewCount())
                .likedByMe(likedByMe)
                .bookmarkedByMe(bookmarkedByMe)
                .isMine(viewerUserId != null && post.isAuthor(viewerUserId))
                .createdAt(post.getCreatedAt())
                .thumbnailImageUrl(thumbnail)
                .build();
    }

    public CommunityResDTO.PostDetail toDetail(
            Post post,
            List<String> imageUrls,
            boolean likedByMe,
            boolean bookmarkedByMe,
            Long viewerUserId
    ) {
        return CommunityResDTO.PostDetail.builder()
                .postId(post.getId())
                .category(post.getCategory())
                .title(post.getTitle())
                .body(post.getBody())
                .tags(post.getTags())
                .imageUrls(imageUrls)
                .author(toAuthor(post.getAuthor()))
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .viewCount(post.getViewCount())
                .likedByMe(likedByMe)
                .bookmarkedByMe(bookmarkedByMe)
                .isMine(viewerUserId != null && post.isAuthor(viewerUserId))
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    public CommunityResDTO.CommentItem toComment(
            Comment comment,
            List<CommunityResDTO.CommentItem> replies,
            boolean likedByMe,
            Long viewerUserId
    ) {
        return CommunityResDTO.CommentItem.builder()
                .commentId(comment.getId())
                .postId(comment.getPost().getId())
                .parentCommentId(comment.getParent() != null ? comment.getParent().getId() : null)
                .body(comment.getBody())
                .author(toAuthor(comment.getAuthor()))
                .likeCount(comment.getLikeCount())
                .likedByMe(likedByMe)
                .isMine(viewerUserId != null && comment.isAuthor(viewerUserId))
                .createdAt(comment.getCreatedAt())
                .replies(replies)
                .build();
    }

    public boolean isLiked(Set<Long> likedIds, Long postId) {
        return likedIds != null && likedIds.contains(postId);
    }

    public boolean isBookmarked(Set<Long> bookmarkedIds, Long postId) {
        return bookmarkedIds != null && bookmarkedIds.contains(postId);
    }
}
