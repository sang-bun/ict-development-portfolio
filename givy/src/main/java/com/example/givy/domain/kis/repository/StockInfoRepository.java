package com.example.givy.domain.kis.repository;

import com.example.givy.domain.kis.entity.StockInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockInfoRepository extends JpaRepository<StockInfo, String> {
    // 기본 CRUD 메서드는 JpaRepository가 제공합니다.
    // 필요 시 커스텀 쿼리 메서드를 여기에 추가합니다.
}
