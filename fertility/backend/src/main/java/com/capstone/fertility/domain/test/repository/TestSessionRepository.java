package com.capstone.fertility.domain.test.repository;

import com.capstone.fertility.domain.test.entity.TestSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestSessionRepository extends JpaRepository<TestSession, Long> {

    boolean existsByIdAndUser_Id(Long sessionId, Long userId);
}
