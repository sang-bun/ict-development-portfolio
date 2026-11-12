package umc.fitty.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import umc.fitty.apiPayload.ApiResponse;
import umc.fitty.converter.CharacterConverter;
import umc.fitty.domain.Character;
import umc.fitty.service.CharacterService.CharacterCommandService;
import umc.fitty.service.CharacterService.CharacterQueryService;
import umc.fitty.web.dto.CharacterDTO.CharacterRequestDTO;
import umc.fitty.web.dto.CharacterDTO.CharacterResponseDTO;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/me/character")
public class CharacterRestController {

    private final CharacterCommandService characterCommandService;
    private final CharacterQueryService characterQueryService;


    @PostMapping
    public ApiResponse<CharacterResponseDTO.CreateCharacterResultDTO> createCharacter(@RequestBody CharacterRequestDTO.CreateCharacterDTO request) {
        Character character = characterCommandService.createCharacter(request);
        return ApiResponse.success("캐릭터가 성공적으로 생성되었습니다.", CharacterConverter.toCharacterResultDTO(character));
    }

    @GetMapping
    public ApiResponse<CharacterResponseDTO.CharacterDetailDTO> getCharacter() {
        Character character = characterQueryService.findMyCharacter();
        return ApiResponse.success("캐릭터 조회에 성공했습니다.", CharacterConverter.toCharacterDetailDTO(character));
    }

    @PatchMapping
    public ApiResponse<CharacterResponseDTO.CharacterUpdateResultDTO> updateCharacter(@Valid @RequestBody CharacterRequestDTO.UpdateCharacterDTO request) {
        Character character = characterCommandService.updateCharacter(request);
        return ApiResponse.success("캐릭터 정보 수정에 성공했습니다.", CharacterConverter.toCharacterUpdateResultDTO(character));
    }
}
