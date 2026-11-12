package umc.fitty.web.dto.GoalDTO;

import lombok.*;
import umc.fitty.domain.enums.GoalType;

import java.time.LocalDate;
import java.util.List;

@Getter @Builder
public class GoalDetailResponseDTO {
    private Long id;
    private GoalType type;

    private Integer targetCount;
    private Integer progressCount;
    private Double  targetDistance;
    private Double  progressDistance;
    private Integer targetTime;
    private Integer progressTime;

    private LocalDate weekStartDate; // 월요일
    private LocalDate weekEndDate;   // 일요일
    private String   summary;        // 예) "일주일간 4번 달리기"
    private String   achievement;    // 예) "이번 주 러닝 목표 중 3/4번 달성!"

    private List<DayStatusDTO> days; // 월~일 표시용
}

