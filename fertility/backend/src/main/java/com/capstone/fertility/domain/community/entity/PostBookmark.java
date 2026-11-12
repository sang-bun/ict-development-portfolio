package com.capstone.fertility.domain.community.entity;

import com.capstone.fertility.domain.user.entity.User;
import com.capstone.fertility.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "community_post_bookmarks",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_community_post_bookmarks_post_user",
                columnNames = {"post_id", "user_id"}
        ),
        indexes = {
                @Index(name = "idx_community_post_bookmarks_post", columnList = "post_id"),
                @Index(name = "idx_community_post_bookmarks_user", columnList = "user_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PostBookmark extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_bookmark_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
