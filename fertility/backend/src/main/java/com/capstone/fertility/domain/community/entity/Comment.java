package com.capstone.fertility.domain.community.entity;

import com.capstone.fertility.domain.community.enums.CommentStatus;
import com.capstone.fertility.domain.user.entity.User;
import com.capstone.fertility.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "community_comments",
        indexes = {
                @Index(name = "idx_community_comments_post", columnList = "post_id"),
                @Index(name = "idx_community_comments_parent", columnList = "parent_comment_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private Comment parent;

    @Column(name = "body", length = 1000, nullable = false)
    private String body;

    @Builder.Default
    @Column(name = "like_count", nullable = false)
    private int likeCount = 0;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "status", length = 20, nullable = false)
    private CommentStatus status = CommentStatus.ACTIVE;

    public boolean isActive() {
        return status == CommentStatus.ACTIVE;
    }

    public boolean isAuthor(Long userId) {
        return author != null && author.getId().equals(userId);
    }

    public void updateBody(String body) {
        this.body = body;
    }

    public void softDelete() {
        this.status = CommentStatus.DELETED;
    }

    public void adjustLikeCount(int delta) {
        this.likeCount = Math.max(0, this.likeCount + delta);
    }
}
