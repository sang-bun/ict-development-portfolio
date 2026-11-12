package com.capstone.fertility.domain.community.repository;

import com.capstone.fertility.domain.community.entity.Post;
import com.capstone.fertility.domain.community.enums.PostCategory;
import com.capstone.fertility.domain.community.enums.PostStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CommunityPostRepository extends JpaRepository<Post, Long>, CommunityPostQueryRepository {

    @Query("SELECT p FROM Post p JOIN FETCH p.author WHERE p.id = :id AND p.status = :status")
    Optional<Post> findActiveByIdWithAuthor(@Param("id") Long id, @Param("status") PostStatus status);

    @Query("SELECT p FROM Post p JOIN FETCH p.author WHERE p.id = :id")
    Optional<Post> findByIdWithAuthor(@Param("id") Long id);

    boolean existsByIdAndStatus(Long id, PostStatus status);

    long countByCategoryAndStatus(PostCategory category, PostStatus status);
}
