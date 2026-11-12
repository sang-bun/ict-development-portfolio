package umc.fitty.web.dto.UserDTO;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserMeResponseDTO {
    private Long id;
    private String email;
    private String nickname;         // effective 값
    private String profileImageUrl;  // effective 값
}