package umc.fitty.web.dto.GoalDTO;

import lombok.*;
import umc.fitty.domain.enums.GoalType;

import java.time.LocalDate;

@Getter
@Builder
public class GoalResponseDTO {
    private Long id;
    private GoalType type;
    private Integer targetCount;
    private Double  targetDistance;
    private Integer targetTime;

    private Integer progressCount;
    private Double  progressDistance;
    private Integer progressTime;

    private LocalDate weekStartDate;
    private String summary;
}