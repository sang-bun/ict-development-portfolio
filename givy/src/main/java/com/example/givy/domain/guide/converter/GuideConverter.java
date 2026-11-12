package com.example.givy.domain.guide.converter;

import com.example.givy.domain.guide.dto.res.GuideResDTO;
import com.example.givy.domain.guide.entity.Guide;

public class GuideConverter {

    /* 04-01, 04-05 목록용 */
    public static GuideResDTO.GuideSummaryDTO toSummaryDTO(Guide guide) {
        return GuideResDTO.GuideSummaryDTO.builder()
                .guideId(guide.getGuideId())
                .title(guide.getTitle())
                .imageUrl(guide.getImageUrl())
                .category(guide.getCategory())
                .likeCount(guide.getLikeCount())
                .storeCount(guide.getStoreCount())
                .build();
    }

    /* 04-02 상세 조회용 */
    public static GuideResDTO.GuideDetailDTO toDetailDTO(
            Guide guide,
            boolean isLiked,
            boolean isStored
    ) {
        return GuideResDTO.GuideDetailDTO.builder()
                .guideId(guide.getGuideId())
                .title(guide.getTitle())
                .body(guide.getBody())
                .imageUrl(guide.getImageUrl())
                .category(guide.getCategory())
                .likeCount(guide.getLikeCount())
                .storeCount(guide.getStoreCount())
                .isLiked(isLiked)
                .isStored(isStored)
                .build();
    }
}
