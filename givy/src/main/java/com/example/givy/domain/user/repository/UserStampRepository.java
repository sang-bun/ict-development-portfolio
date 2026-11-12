package com.example.givy.domain.user.repository;

import com.example.givy.domain.user.entity.UserStamp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserStampRepository extends JpaRepository<UserStamp, Long> {
    boolean existsByUsers_UserId(Long userId);
}
