package com.example.givy.domain.guide.entity;

import com.example.givy.domain.user.entity.Users;
import com.example.givy.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="guide_like")
public class GuideLike extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="guide_like_id")
    private Long guideLikeId;

    @Column(name="is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    //mapping
    @ManyToOne
    @JoinColumn(name="user_id")
    private Users users;

    @ManyToOne
    @JoinColumn(name="guide_id")
    private Guide guide;

    public void delete() {
        this.isDeleted = true;
    }

    public void restore() {
        this.isDeleted = false;
    }

}
