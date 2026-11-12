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
@Table(name="buy_history")
public class BuyHistory extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="buy_history_id")
    private Long buyHistoryId;

    @Column(name="purchase_price", nullable = false)
    private Long purchasePrice;

    @Column(name="quantity", nullable = false)
    private Long quantity;

    //mapping
    @ManyToOne
    @JoinColumn(name="user_id")
    private Users users;

    @ManyToOne
    @JoinColumn(name="product_id")
    private Product product;
}