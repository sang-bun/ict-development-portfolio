package com.capstone.fertility.domain.community.entity;

import com.capstone.fertility.domain.community.enums.PostCategory;
import com.capstone.fertility.domain.community.enums.PostStatus;
import com.capstone.fertility.domain.user.entity.User;
import com.capstone.fertility.global.common.BaseEntity;
import com.capstone.fertility.global.common.jpa.StringListJsonConverter;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "community_posts",
        indexes = {
                @Index(name = "idx_community_posts_user", columnList = "user_id"),
                @Index(name = "idx_community_posts_category", columnList = "category"),
                @Index(name = "idx_community_posts_status_created", columnList = "status, created_at")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", length = 30, nullable = false)
    private PostCategory category;

    @Column(name = "title", length = 50, nullable = false)
    private String title;

    @Column(name = "body", length = 4000, nullable = false)
    private String body;

    @Convert(converter = StringListJsonConverter.class)
    @Column(name = "tags", columnDefinition = "TEXT")
    @Builder.Default
    private List<String> tags = new ArrayList<>();

    @Builder.Default
    @Column(name = "like_count", nullable = false)
    private int likeCount = 0;

    @Builder.Default
    @Column(name = "comment_count", nullable = false)
    private int commentCount = 0;

    @Builder.Default
    @Column(name = "view_count", nullable = false)
    private int viewCount = 0;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "status", length = 20, nullable = false)
    private PostStatus status = PostStatus.ACTIVE;

    public boolean isActive() {
        return status == PostStatus.ACTIVE;
    }

    public boolean isAuthor(Long userId) {
        return author != null && author.getId().equals(userId);
    }

    public void updateContent(PostCategory category, String title, String body, List<String> tags) {
        if (category != null) {
            this.category = category;
        }
        if (title != null) {
            this.title = title;
        }
        if (body != null) {
            this.body = body;
        }
        if (tags != null) {
            this.tags = tags;
        }
    }

    public void softDelete() {
        this.status = PostStatus.DELETED;
    }

    public void incrementViewCount() {
        this.viewCount++;
    }

    public void adjustLikeCount(int delta) {
        this.likeCount = Math.max(0, this.likeCount + delta);
    }

    public void adjustCommentCount(int delta) {
        this.commentCount = Math.max(0, this.commentCount + delta);
    }
}
