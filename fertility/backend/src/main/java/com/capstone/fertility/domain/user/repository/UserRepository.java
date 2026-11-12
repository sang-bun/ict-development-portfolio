package com.capstone.fertility.domain.user.repository;

import com.capstone.fertility.domain.user.entity.User;
import com.capstone.fertility.domain.user.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByKakaoId(Long kakaoId);

    Optional<User> findByPartnerCode(String partnerCode);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByEmailAndStatus(String email, UserStatus status);

    List<User> findAllByPartner_Id(Long partnerId);

    List<User> findByLastMissionDateBefore(LocalDateTime cutoff);
}