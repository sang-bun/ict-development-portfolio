package com.capstone.fertility.domain.mission.repository;

import com.capstone.fertility.domain.mission.entity.MissionSproutLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MissionSproutLogRepository extends JpaRepository<MissionSproutLog, Long> {

    List<MissionSproutLog> findByUser_IdOrderByIdDesc(Long userId, Pageable pageable);

    List<MissionSproutLog> findByUser_IdAndIdLessThanOrderByIdDesc(Long userId, Long lastLogId, Pageable pageable);
}
