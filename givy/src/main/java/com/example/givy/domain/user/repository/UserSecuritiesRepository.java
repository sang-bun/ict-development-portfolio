package com.example.givy.domain.user.repository;

import com.example.givy.domain.user.entity.UserSecuritiesAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSecuritiesRepository extends JpaRepository<UserSecuritiesAccount, Long> {
    boolean existsByUsers_UserIdAndSecuritiesName(Long userId, String securitiesName);
}
