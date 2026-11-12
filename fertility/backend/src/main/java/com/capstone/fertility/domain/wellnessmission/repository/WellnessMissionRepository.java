package com.capstone.fertility.domain.wellnessmission.repository;

import com.capstone.fertility.domain.wellnessmission.entity.WellnessMission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WellnessMissionRepository extends JpaRepository<WellnessMission, Long> {

    List<WellnessMission> findAllByTestResultIdOrderByIdAsc(Long testResultId);

    List<WellnessMission> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    boolean existsByTestResultId(Long testResultId);

    List<WellnessMission> findByUser_IdAndTestResult_IdOrderByIdAsc(Long userId, Long testResultId);

    @Query("select max(m.testResult.id) from WellnessMission m where m.user.id = :userId")
    Optional<Long> findMaxTestResultIdByUserId(@Param("userId") Long userId);
}
