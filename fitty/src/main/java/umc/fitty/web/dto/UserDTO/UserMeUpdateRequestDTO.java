package umc.fitty.web.dto.UserDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserMeUpdateRequestDTO {
    private String nickname;         // null → 카카오 닉네임으로 되돌림
    private String profileImageUrl;  // null → 카카오 프로필 이미지로 되돌림
}
