package com.example.givy.global.auth.repository;

import com.example.givy.global.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    void deleteAllByUserId(Long userId);

    @Modifying
    @Query("""
    DELETE FROM RefreshToken rt
    WHERE rt.expiredAt < :now
       OR rt.revokedAt IS NOT NULL
""")
    void deleteExpiredOrRevoked(@Param("now") LocalDateTime now);

}
