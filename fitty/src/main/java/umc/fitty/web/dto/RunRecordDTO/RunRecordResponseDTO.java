package umc.fitty.web.dto.RunRecordDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class RunRecordResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecordResultDTO{

        private Long recordId;
        private LocalDateTime createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecordDetailDTO{
        private long recordId;
        private Double distance;
        private Integer durationMin;
        private String diary;
        private String imageUrl;
        private LocalDateTime createdAt;
    }
}
