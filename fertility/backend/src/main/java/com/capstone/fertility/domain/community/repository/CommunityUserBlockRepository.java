package com.capstone.fertility.domain.community.repository;

import com.capstone.fertility.domain.community.entity.CommunityUserBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface CommunityUserBlockRepository extends JpaRepository<CommunityUserBlock, Long> {

    boolean existsByBlocker_IdAndBlocked_Id(Long blockerId, Long blockedId);

    @Query("SELECT b.blocked.id FROM CommunityUserBlock b WHERE b.blocker.id = :blockerId")
    Set<Long> findBlockedUserIds(@Param("blockerId") Long blockerId);

    @Query("SELECT b.blocked.id FROM CommunityUserBlock b WHERE b.blocker.id = :blockerId AND b.blocked.id IN :userIds")
    Set<Long> findBlockedAmong(@Param("blockerId") Long blockerId, @Param("userIds") List<Long> userIds);
}
