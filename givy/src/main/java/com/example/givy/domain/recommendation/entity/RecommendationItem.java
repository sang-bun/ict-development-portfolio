package com.example.givy.domain.recommendation.entity;

import com.example.givy.domain.commerce.entity.Product;
import com.example.givy.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name="recommendation_item")
public class RecommendationItem extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="recommendation_item_id")
    private Long recommendationItemId;

    //mapping
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="recommendation_event_id")
    private RecommendationEvent recommendationEvent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="product_id")
    private Product product;
}
