package com.example.givy.domain.kis.entity;

import com.example.givy.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "stock_info")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class StockInfo extends BaseEntity {

    @Id
    @Column(length = 10, nullable = false)
    private String ticker; // 종목코드 (PK)

    @Column(length = 50)
    private String name; // 종목명 (선택)

    @Column(name = "current_price")
    private Long currentPrice; // 현재가

    @Column(name = "nav")
    private Double nav; // 순자산가치 (ETF)

    @Column(name = "highest_price_52w")
    private Long highestPrice52w; // 52주 최고가

    @Column(name = "lowest_price_52w")
    private Long lowestPrice52w; // 52주 최저가

    // 데이터 갱신을 위한 편의 메서드
    public void updatePrices(Long currentPrice, Double nav, Long highestPrice52w, Long lowestPrice52w, String name) {
        this.currentPrice = currentPrice;
        this.nav = nav;
        this.highestPrice52w = highestPrice52w;
        this.lowestPrice52w = lowestPrice52w;
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
    }
}
