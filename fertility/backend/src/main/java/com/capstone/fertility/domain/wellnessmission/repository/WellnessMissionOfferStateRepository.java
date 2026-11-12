package com.capstone.fertility.domain.wellnessmission.repository;

import com.capstone.fertility.domain.wellnessmission.entity.WellnessMissionOfferState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WellnessMissionOfferStateRepository extends JpaRepository<WellnessMissionOfferState, Long> {

    Optional<WellnessMissionOfferState> findByUser_IdAndTestResult_Id(Long userId, Long testResultId);
}
