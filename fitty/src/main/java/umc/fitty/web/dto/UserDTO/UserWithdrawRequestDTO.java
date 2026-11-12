package umc.fitty.web.dto.UserDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserWithdrawRequestDTO {
    @Schema(description = "탈퇴 사유", example = "서비스 이용이 불편했어요")
    private String reason;

    @Schema(description = "추가 피드백", example = "기록 화면 동선이 헷갈립니다")
    private String feedback;
}
