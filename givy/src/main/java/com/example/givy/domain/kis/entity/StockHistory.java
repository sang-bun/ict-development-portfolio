package com.example.givy.domain.kis.entity;

import com.example.givy.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "stock_history",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_stock_history_ticker_date",
                        columnNames = {"ticker", "base_date"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class StockHistory extends BaseEntity { // 일별시세정보를 저장하는 엔티티

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 10, nullable = false)
    private String ticker; // 종목코드

    @Column(name = "base_date", nullable = false)
    private LocalDate baseDate; // 기준일자 (YYYY-MM-DD)

    @Column(name = "close_price", nullable = false)
    private Long closePrice; // 종가

    // 데이터 갱신을 위한 편의 메서드
    public void updatePrice(Long closePrice) {
        this.closePrice = closePrice;
    }
}
