package com.capstone.fertility.domain.community.repository;

import com.capstone.fertility.domain.community.entity.PostBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface CommunityPostBookmarkRepository extends JpaRepository<PostBookmark, Long> {

    Optional<PostBookmark> findByPost_IdAndUser_Id(Long postId, Long userId);

    boolean existsByPost_IdAndUser_Id(Long postId, Long userId);

    @Query("SELECT pb.post.id FROM PostBookmark pb WHERE pb.user.id = :userId AND pb.post.id IN :postIds")
    Set<Long> findBookmarkedPostIds(@Param("userId") Long userId, @Param("postIds") Collection<Long> postIds);

    boolean existsByUser_IdAndPost_Id(Long userId, Long postId);
}
