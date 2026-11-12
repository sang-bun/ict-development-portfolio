package com.example.givy.domain.user.repository;

import com.example.givy.domain.user.entity.UserSecuritiesAccount;
import com.example.givy.domain.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserSecuritiesAccountRepository extends JpaRepository<UserSecuritiesAccount,Long> {
    Optional<UserSecuritiesAccount> findByUsers(Users user);
    
    // 사용자의 모든 증권 계좌 조회
    List<UserSecuritiesAccount> findAllByUsers(Users user);
    
    // 가장 최근 증권 계좌 조회
    Optional<UserSecuritiesAccount> findFirstByUsersOrderByCreatedAtDesc(Users user);
}
