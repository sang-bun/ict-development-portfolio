package umc.fitty.web.dto.NotificationDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NotificationPatchRequestDTO {

    @Schema(description = "푸시 알림 on/off")
    private Boolean pushEnabled;

    @Schema(description = "운동 리마인더 on/off")
    private Boolean reminderEnabled;

    @Schema(description = "운동 리마인더 시간 (HH:mm)")
    @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "reminderTime은 HH:mm 형식이어야 합니다.")
    private String reminderTime;
}
