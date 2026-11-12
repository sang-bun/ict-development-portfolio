package com.capstone.fertility.domain.community.repository;

import com.capstone.fertility.domain.community.entity.Comment;
import com.capstone.fertility.domain.community.enums.CommentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommunityCommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c JOIN FETCH c.author WHERE c.id = :id")
    Optional<Comment> findByIdWithAuthor(@Param("id") Long id);

    List<Comment> findByPost_IdAndParentIsNullAndStatusOrderByCreatedAtAsc(
            Long postId,
            CommentStatus status
    );

    List<Comment> findByParent_IdAndStatusOrderByCreatedAtAsc(Long parentId, CommentStatus status);

    long countByPost_IdAndStatus(Long postId, CommentStatus status);
}
