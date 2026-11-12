package com.example.givy.domain.guide.repository;

import com.example.givy.domain.guide.dto.res.GuideResDTO;
import com.example.givy.domain.guide.enums.GuideCategory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CustomGuideRepository {
    Slice<GuideResDTO.GuideSummaryDTO> findGuides(
            GuideCategory category,
            Pageable pageable
    );
}
