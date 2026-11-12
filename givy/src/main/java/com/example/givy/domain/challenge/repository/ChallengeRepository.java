package com.example.givy.domain.challenge.repository;

import com.example.givy.domain.challenge.entity.StartChallenge;
import com.example.givy.domain.challenge.enums.Status;
import com.example.givy.domain.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChallengeRepository extends JpaRepository<StartChallenge,Long> {
    Optional<StartChallenge> findByUsers(Users users);
    
    // 진행 중인 챌린지 조회 (가장 최근 것)
    Optional<StartChallenge> findFirstByUsersAndStatusOrderByCreatedAtDesc(Users users, Status status);
    
    // 사용자의 모든 챌린지 조회
    List<StartChallenge> findAllByUsers(Users users);
}
