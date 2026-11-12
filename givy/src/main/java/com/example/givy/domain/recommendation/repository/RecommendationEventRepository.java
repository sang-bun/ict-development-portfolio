package com.example.givy.domain.recommendation.repository;

import com.example.givy.domain.recommendation.entity.RecommendationEvent;
import com.example.givy.domain.recommendation.entity.Tendency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecommendationEventRepository extends JpaRepository<RecommendationEvent, Long> {
    Optional<RecommendationEvent> findTopByTendencyOrderByCreatedAtDesc(Tendency tendency);
    
    List<RecommendationEvent> findAllByTendency(Tendency tendency);
    
    void deleteAllByTendency(Tendency tendency);
}
