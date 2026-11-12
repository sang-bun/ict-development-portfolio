package com.capstone.fertility.domain.community.dto.res;

import com.capstone.fertility.domain.community.enums.PostCategory;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public class CommunityResDTO {

    @Getter
    @Builder
    public static class Author {
        private final Long userId;
        private final String nickname;
        private final String profileImageUrl;
    }

    @Getter
    @Builder
    public static class PostSummary {
        private final Long postId;
        private final PostCategory category;
        private final String title;
        private final String bodyPreview;
        private final List<String> tags;
        private final Author author;
        private final int likeCount;
        private final int commentCount;
        private final int viewCount;
        private final boolean likedByMe;
        private final boolean bookmarkedByMe;
        private final boolean isMine;
        private final LocalDateTime createdAt;
        private final String thumbnailImageUrl;
    }

    @Getter
    @Builder
    public static class PostDetail {
        private final Long postId;
        private final PostCategory category;
        private final String title;
        private final String body;
        private final List<String> tags;
        private final List<String> imageUrls;
        private final Author author;
        private final int likeCount;
        private final int commentCount;
        private final int viewCount;
        private final boolean likedByMe;
        private final boolean bookmarkedByMe;
        private final boolean isMine;
        private final LocalDateTime createdAt;
        private final LocalDateTime updatedAt;
    }

    @Getter
    @Builder
    public static class PostPage {
        private final List<PostSummary> content;
        private final int page;
        private final int size;
        private final long totalElements;
        private final int totalPages;
        private final boolean hasNext;
    }

    @Getter
    @Builder
    public static class LikeToggle {
        private final boolean liked;
        private final int likeCount;
    }

    @Getter
    @Builder
    public static class BookmarkToggle {
        private final boolean bookmarked;
    }

    @Getter
    @Builder
    public static class CommentItem {
        private final Long commentId;
        private final Long postId;
        private final Long parentCommentId;
        private final String body;
        private final Author author;
        private final int likeCount;
        private final boolean likedByMe;
        private final boolean isMine;
        private final LocalDateTime createdAt;
        private final List<CommentItem> replies;
    }

    @Getter
    @Builder
    public static class CommentPage {
        private final List<CommentItem> content;
        private final int page;
        private final int size;
        private final long totalElements;
        private final int totalPages;
        private final boolean hasNext;
    }

    @Getter
    @Builder
    public static class ImageUpload {
        private final String imageUrl;
    }
}
