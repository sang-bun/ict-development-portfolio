package com.example.givy.domain.recommendation.repository;

import com.example.givy.domain.recommendation.entity.Tendency;
import com.example.givy.domain.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TendencyRepository extends JpaRepository<Tendency, Long> {
    Optional<Tendency> findByUsers(Users users);

    Optional<Tendency> findTopByUsers_UserIdOrderByCreatedAtDesc(Long userId);
}
