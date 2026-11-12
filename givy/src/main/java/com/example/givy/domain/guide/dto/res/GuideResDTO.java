package com.example.givy.domain.guide.dto.res;

import com.example.givy.domain.guide.enums.GuideCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class GuideResDTO {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class GuideSummaryDTO {
        private Long guideId;
        private String title;
        private String imageUrl;
        private Long likeCount;
        private Long storeCount;
        private GuideCategory category;

    }

    @Getter
    @Builder
    public static class GuideDetailDTO {
        private Long guideId;
        private String title;
        private String body;
        private String imageUrl;
        private GuideCategory category;
        private Long likeCount;
        private Long storeCount;
        private Boolean isLiked;
        private Boolean isStored;
    }
}

