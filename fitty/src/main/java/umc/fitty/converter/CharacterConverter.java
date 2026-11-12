package umc.fitty.converter;

import umc.fitty.domain.Character;
import umc.fitty.domain.enums.CharacterType;
import umc.fitty.web.dto.CharacterDTO.CharacterRequestDTO;
import umc.fitty.web.dto.CharacterDTO.CharacterResponseDTO;

public class CharacterConverter {

    public static Character toCharacter(CharacterRequestDTO.CreateCharacterDTO request){

        CharacterType characterType = CharacterType.valueOf(request.getType().toUpperCase());

        return Character.builder()
                .name(request.getName())
                .type(characterType)
                .build();
    }

    public static CharacterResponseDTO.CreateCharacterResultDTO toCharacterResultDTO(Character character){
        return CharacterResponseDTO.CreateCharacterResultDTO.builder()
                .characterId(character.getId())
                .createdAt(character.getCreatedAt())
                .build();
    }

    public static CharacterResponseDTO.CharacterDetailDTO toCharacterDetailDTO(Character character) {

        String characterType = character.getType().toString();

        return CharacterResponseDTO.CharacterDetailDTO.builder()
                .characterId(character.getId())
                .userId(character.getUser().getId())
                .userName(character.getUser().getNickname())
                .characterName(character.getName())
                .level(character.getLevel())
                .type(characterType)
                .updatedAt(character.getUpdatedAt())
                .build();
    }

    public static CharacterResponseDTO.CharacterUpdateResultDTO toCharacterUpdateResultDTO(Character character){

        String characterType = character.getType().toString();

        return CharacterResponseDTO.CharacterUpdateResultDTO.builder()
                .characterId(character.getId())
                .characterName(character.getName())
                .characterType(characterType)
                .updatedAt(character.getUpdatedAt())
                .build();
    }
}
