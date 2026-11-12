package com.example.givy.domain.challenge.repository;

import com.example.givy.domain.challenge.entity.StartChallenge;
import com.example.givy.domain.challenge.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StartChallengeRepository extends JpaRepository<StartChallenge, Long> {
    List<StartChallenge> findAllByUsers_UserId(Long userId);
    Optional<StartChallenge> findByStartChallengeIdAndUsers_UserId(Long startChallengeId, Long userId);
    boolean existsByUsers_UserIdAndProduct_ProductId(Long userId, Long productId);
    boolean existsByUsers_userIdAndStatus(Long userId, Status status);
}
