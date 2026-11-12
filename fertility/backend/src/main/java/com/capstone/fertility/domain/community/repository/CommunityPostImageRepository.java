package com.capstone.fertility.domain.community.repository;

import com.capstone.fertility.domain.community.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommunityPostImageRepository extends JpaRepository<PostImage, Long> {

    List<PostImage> findByPost_IdOrderBySortOrderAsc(Long postId);

    void deleteByPost_Id(Long postId);
}
