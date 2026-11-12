package com.example.givy.domain.recommendation.entity;

import com.example.givy.domain.commerce.entity.Product;
import com.example.givy.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name="recommendation_event")
public class RecommendationEvent extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="recommendation_event_id")
    private Long recommendationEventId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="best_product_id")
    private Product bestProduct;

    //mapping
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="tendency_id",  nullable = false)
    private Tendency tendency;

    @Builder.Default
    @OneToMany(mappedBy="recommendationEvent", cascade = CascadeType.ALL)
    private List<RecommendationItem> recommendationItems = new ArrayList<>();

    public void addItem(RecommendationItem item) {
        this.recommendationItems.add(item);
        item.setRecommendationEvent(this);
    }
}
