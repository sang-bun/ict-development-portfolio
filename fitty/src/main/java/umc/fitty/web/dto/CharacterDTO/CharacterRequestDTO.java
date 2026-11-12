package umc.fitty.web.dto.CharacterDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

public class CharacterRequestDTO {

    @Getter
    public static class CreateCharacterDTO {
        private String name;
        private String type;
    }

    @Getter
    public static class UpdateCharacterDTO {

        @NotBlank(message = "캐릭터 이름은 필수 입력값입니다.")
        @Size(min = 1, max = 10, message = "캐릭터 이름은 1자 이상, 10자 이하로 입력해주세요.")
        private String name;

        @NotBlank(message = "캐릭터 종류는 필수 선택값입니다.")
        private String type;
    }
}
