package com.example.givy.domain.kis.repository;

import com.example.givy.domain.kis.entity.KisToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface KisTokenRepository extends JpaRepository<KisToken, Long> {

    // 가장 최근 토큰 조회
    Optional<KisToken> findFirstByOrderByCreatedAtDesc();

    // 만료된 토큰 삭제
    @Modifying
    @Query("DELETE FROM KisToken kt WHERE kt.expiredAt < :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);
}
