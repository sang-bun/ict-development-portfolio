package umc.fitty.web.dto.GoalDTO;

import lombok.*;
import umc.fitty.domain.enums.GoalType;

@Getter @Setter
public class GoalCreateRequestDTO {
    private GoalType type = GoalType.COUNT; // 기본 회수

    // 하나만 채우도록(컨트롤러에서 검증)
    private Integer targetCount;     // COUNT
    private Double  targetDistance;  // DISTANCE (km)
    private Integer targetTime;      // DURATION (min)
}