package com.example.givy.domain.guide.repository;

import com.example.givy.domain.guide.entity.Guide;
import com.example.givy.domain.guide.entity.GuideStore;
import com.example.givy.domain.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface GuideStoreRespository extends JpaRepository<GuideStore, Long> {

    Optional<GuideStore> findByGuideAndUsers(Guide guide, Users user);
    boolean existsByGuideAndUsersAndIsDeletedFalse(Guide guide, Users users);

    /* 04-05 내가 저장한 가이드 목록 */
    @Query("""
        select gs
        from GuideStore gs
        join fetch gs.guide
        where gs.users = :user
          and gs.isDeleted = false
        order by gs.createdAt desc
    """)
    List<GuideStore> findMyStoredGuides(@Param("user") Users user);
}
