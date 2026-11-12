package com.capstone.fertility.domain.wellnessmission.repository;

import com.capstone.fertility.domain.wellnessmission.entity.WellnessMissionCycleCompletion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface WellnessMissionCycleCompletionRepository extends JpaRepository<WellnessMissionCycleCompletion, Long> {

    @Query("""
            select c.wellnessMission.id from WellnessMissionCycleCompletion c
            where c.user.id = :userId and c.testResultId = :testResultId and c.cycleIndex = :cycleIndex
            """)
    Set<Long> findCompletedMissionIds(
            @Param("userId") Long userId,
            @Param("testResultId") Long testResultId,
            @Param("cycleIndex") int cycleIndex
    );

    boolean existsByUser_IdAndWellnessMission_IdAndCycleIndex(Long userId, Long wellnessMissionId, int cycleIndex);

    long countByUser_IdAndTestResultIdAndCycleIndex(Long userId, Long testResultId, int cycleIndex);
}
