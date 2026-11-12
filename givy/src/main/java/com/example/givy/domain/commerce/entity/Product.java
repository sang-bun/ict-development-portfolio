package com.example.givy.domain.commerce.entity;

import com.example.givy.domain.recommendation.enums.InvestmentType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="product_id")
    private Long productId;

    @Column(name="code", nullable = false)
    private String code;

    @Column(name="name", nullable = false)
    private String name;

    @Column(name="description", nullable = false)
    private String description;

    @Column(name="tagline", nullable = false)
    private String tagline;

    @Column(name="match_reason", nullable = false)
    private String matchReason;

    @Column(name="rate_avg", nullable = false)
    private Double rateAvg;

    @Column(name="rate_max", nullable = false)
    private Double rateMax;

    @Column(name="rate_min", nullable = false)
    private Double rateMin;

    @Column(name="maker_name", nullable = false)
    private String makerName;

    @Column(name="image_url", nullable = false)
    private String imageUrl;

    @Column(name="maker_comment", nullable = false, columnDefinition = "TEXT")
    private String makerComment;

    @Enumerated(EnumType.STRING)
    @Column(name="recommendation_type")
    private InvestmentType recommendationType;

    @Column(name="destination", nullable = false)
    private String destination;

    @Column(name="theme", nullable = false)
    private String theme;

    @Column(name="exchange", nullable = false)
    private String exchange;

    @Column(name="description_title", nullable = false)
    private String descriptionTitle;

    @Column(name = "ticker", nullable = false)
    private String ticker;
}
