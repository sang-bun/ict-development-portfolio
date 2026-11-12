package umc.fitty.web.dto.RunRecordDTO;

import lombok.Getter;


public class RunRecordRequestDTO {

    @Getter
    public static class CreateRecordDTO{

        private Double distance;
        private Integer durationMin;
        private String diary;
        private String imageUrl;
    }
}
