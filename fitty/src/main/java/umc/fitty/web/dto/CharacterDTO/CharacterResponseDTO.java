package umc.fitty.web.dto.CharacterDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import umc.fitty.domain.User;

import java.time.LocalDateTime;

public class CharacterResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateCharacterResultDTO{

            private Long characterId;
            private LocalDateTime createdAt;

    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CharacterDetailDTO {
        private Long characterId;
        private Long userId;
        private String userName;
        private String characterName;
        private int level;
        private String type;
        private LocalDateTime updatedAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CharacterUpdateResultDTO{
        private Long characterId;
        private String characterName;
        private String characterType;
        private LocalDateTime updatedAt;

    }
}
