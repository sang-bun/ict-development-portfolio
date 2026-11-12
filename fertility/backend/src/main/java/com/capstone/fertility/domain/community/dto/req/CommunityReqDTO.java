package com.capstone.fertility.domain.community.dto.req;

import com.capstone.fertility.domain.community.enums.ReportReason;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

public class CommunityReqDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class PostWrite {
        @Schema(example = "QA")
        private String category;
        @Schema(example = "수면 시간 질문")
        private String title;
        private String body;
        private List<String> tags;
        private List<String> imageUrls;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CommentWrite {
        private String body;
        private Long parentCommentId;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class PostReport {
        private ReportReason reason;
        private String detail;
    }
}
