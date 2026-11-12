package umc.fitty.web.dto.GoalDTO;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter @Builder
public class DayStatusDTO {
    private String dayLabel;     // "월","화","수","목","금","토","일"
    private LocalDate date;      // 해당 날짜
    private String status;       // DONE | MISS | FUTURE
    private int runCount;        // 그 날 기록 개수
}
