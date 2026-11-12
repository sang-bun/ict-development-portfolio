package com.example.givy.domain.guide.repository;

import com.example.givy.domain.guide.entity.Guide;
import com.example.givy.domain.guide.entity.GuideLike;
import com.example.givy.domain.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GuideLikeRepository extends JpaRepository<GuideLike, Long> {

    Optional<GuideLike> findByGuideAndUsers(Guide guide, Users user);
    boolean existsByGuideAndUsersAndIsDeletedFalse(Guide guide, Users users);
}
