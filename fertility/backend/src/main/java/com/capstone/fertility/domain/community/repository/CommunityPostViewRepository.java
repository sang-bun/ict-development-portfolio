package com.capstone.fertility.domain.community.repository;

import com.capstone.fertility.domain.community.entity.PostView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommunityPostViewRepository extends JpaRepository<PostView, Long> {

    Optional<PostView> findByPost_IdAndUser_Id(Long postId, Long userId);
}
