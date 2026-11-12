package com.example.givy.domain.guide.service.query;

import com.example.givy.domain.guide.dto.res.GuideResDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface GuideQueryService {

    /* 04-02 가이드 상세 */
    GuideResDTO.GuideDetailDTO getGuideDetail(Long guideId, Long userId);

    /* 04-05 내가 저장한 가이드 */
    List<GuideResDTO.GuideSummaryDTO> getMyStoredGuides(Long userId);

    /* 04-06 가이드 목록 조회(카테고리 선택형, Slice) */
    Slice<GuideResDTO.GuideSummaryDTO> getGuides(String category, Pageable pageable);
}
