package com.capstone.fertility.domain.community.repository;

import com.capstone.fertility.domain.community.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CommunityPostLikeRepository extends JpaRepository<PostLike, Long> {

    Optional<PostLike> findByPost_IdAndUser_Id(Long postId, Long userId);

    boolean existsByPost_IdAndUser_Id(Long postId, Long userId);

    @Query("SELECT pl.post.id FROM PostLike pl WHERE pl.user.id = :userId AND pl.post.id IN :postIds")
    Set<Long> findLikedPostIds(@Param("userId") Long userId, @Param("postIds") Collection<Long> postIds);

    List<PostLike> findByUser_Id(Long userId);
}
