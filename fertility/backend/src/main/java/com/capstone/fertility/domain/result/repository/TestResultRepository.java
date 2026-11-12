package com.capstone.fertility.domain.result.repository;

import com.capstone.fertility.domain.result.entity.TestResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TestResultRepository extends JpaRepository<TestResult, Long> {

    Optional<TestResult> findByTestSession_Id(Long sessionId);

    /** 홈 카드용: 사용자의 가장 최근 검사 결과 1건. */
    Optional<TestResult> findFirstByUser_IdOrderByCreatedAtDesc(Long userId);

    /**
     * 히스토리 조회(연/월 단위).
     * - userId 기준
     * - createdAt 기간 조건
     */
    @Query("""
            SELECT t
            FROM TestResult t
            WHERE t.user.id = :userId
              AND t.createdAt >= :startInclusive
              AND t.createdAt < :endExclusive
            ORDER BY t.createdAt DESC
            """)
    List<TestResult> findByUserIdAndCreatedAtInMonth(
            @Param("userId") Long userId,
            @Param("startInclusive") LocalDateTime startInclusive,
            @Param("endExclusive") LocalDateTime endExclusive
    );
}
