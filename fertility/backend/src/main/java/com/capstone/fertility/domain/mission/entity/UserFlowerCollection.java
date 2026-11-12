package com.capstone.fertility.domain.mission.entity;

import com.capstone.fertility.domain.mission.enums.FlowerType;
import com.capstone.fertility.domain.user.entity.User;
import com.capstone.fertility.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "user_flower_collections",
        uniqueConstraints = @UniqueConstraint(name = "uk_user_flower", columnNames = {"user_id", "flower_type"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserFlowerCollection extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "collection_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "flower_type", length = 30, nullable = false)
    private FlowerType flowerType;

    @Column(name = "achieved_at", nullable = false)
    private LocalDateTime achievedAt;
}
