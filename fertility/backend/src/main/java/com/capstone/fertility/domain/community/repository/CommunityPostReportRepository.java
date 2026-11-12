package com.capstone.fertility.domain.community.repository;

import com.capstone.fertility.domain.community.entity.PostReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityPostReportRepository extends JpaRepository<PostReport, Long> {

    boolean existsByPost_IdAndReporter_Id(Long postId, Long reporterId);
}
