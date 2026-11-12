package umc.fitty.web.dto.NotificationDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@AllArgsConstructor
@Builder
public class NotificationResponseDTO {

    @Schema(description = "푸시 알림 전체 on/off", example = "true")
    private boolean pushEnabled;

    @Schema(description = "운동 리마인더 on/off", example = "true")
    private boolean reminderEnabled;

    @Schema(description = "운동 리마인더 시간 (HH:mm)", example = "08:30")
    private String reminderTime;
}
