package com.capstone.fertility.domain.mission.repository;

import com.capstone.fertility.domain.mission.entity.UserFlowerCollection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserFlowerCollectionRepository extends JpaRepository<UserFlowerCollection, Long> {

    List<UserFlowerCollection> findByUser_IdOrderByAchievedAtDesc(Long userId);
}
