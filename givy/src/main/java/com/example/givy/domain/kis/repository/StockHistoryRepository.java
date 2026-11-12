package com.example.givy.domain.kis.repository;

import com.example.givy.domain.kis.entity.StockHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface StockHistoryRepository extends JpaRepository<StockHistory, Long> {
    
    // 특정 종목, 특정 날짜의 데이터가 이미 있는지 확인용
    Optional<StockHistory> findByTickerAndBaseDate(String ticker, LocalDate baseDate);
    
    // 특정 종목의 가장 최근 데이터 날짜 확인용 (어디까지 수집했나 확인)
    Optional<StockHistory> findTopByTickerOrderByBaseDateDesc(String ticker);
}
