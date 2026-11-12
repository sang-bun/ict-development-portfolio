package umc.fitty.service.CharacterService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.fitty.apiPayload.code.ErrorCode;
import umc.fitty.apiPayload.exception.CustomException;
import umc.fitty.converter.CharacterConverter;
import umc.fitty.domain.Character;
import umc.fitty.domain.User;
import umc.fitty.domain.enums.CharacterType;
import umc.fitty.repository.CharacterRepository;
import umc.fitty.repository.UserRepository;
import umc.fitty.web.dto.CharacterDTO.CharacterRequestDTO;

@Service
@RequiredArgsConstructor
@Transactional
public class CharacterCommandImpl implements CharacterCommandService{

    private final CharacterRepository characterRepository;
    private final UserRepository userRepository;


    @Override
    public Character createCharacter(CharacterRequestDTO.CreateCharacterDTO request) {
        User user = findCurrentUser();

        if (characterRepository.existsByUser(user)){
            throw new CustomException(ErrorCode.CHARACTER_ALREADY_EXISTS);
        }

        Character newCharacter = CharacterConverter.toCharacter(request);

        newCharacter.setUser(user);

        return characterRepository.save(newCharacter);
    }

    @Override
    public Character updateCharacter(CharacterRequestDTO.UpdateCharacterDTO request) {

        User user = findCurrentUser();

        Character character = characterRepository.findByUser(user)
                .orElseThrow(() -> new CustomException(ErrorCode.CHARACTER_NOT_FOUND));

        CharacterType characterType = CharacterType.valueOf(request.getType().toUpperCase());

        character.setNameAndType(request.getName(), characterType);

        return character;
    }

    private User findCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        return userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }


}
