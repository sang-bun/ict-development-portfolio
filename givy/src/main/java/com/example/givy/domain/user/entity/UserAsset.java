package com.example.givy.domain.user.entity;

import com.example.givy.domain.commerce.entity.Product;
import com.example.givy.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="user_asset")
public class UserAsset extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_asset_id")
    private Long assetId;

    @Column(name="quantity")
    private Long quantity;

    @Column(name="average_price")
    private Double averagePrice;

    //mapping
    @ManyToOne
    @JoinColumn(name="user_id")
    private Users users;

    @ManyToOne
    @JoinColumn(name="product_id")
    private Product product;
}
