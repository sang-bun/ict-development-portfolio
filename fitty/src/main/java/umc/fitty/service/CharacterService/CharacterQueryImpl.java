package umc.fitty.service.CharacterService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.fitty.apiPayload.code.ErrorCode;
import umc.fitty.apiPayload.exception.CustomException;
import umc.fitty.domain.User;
import umc.fitty.repository.CharacterRepository;
import umc.fitty.repository.UserRepository;
import umc.fitty.domain.Character;

@Service
@RequiredArgsConstructor
@Transactional
public class CharacterQueryImpl implements CharacterQueryService{

    private final CharacterRepository characterRepository;
    private final UserRepository userRepository;

    @Override
    public Character findMyCharacter() {
        User user = findCurrentUser();

        return characterRepository.findByUser(user)
                .orElseThrow(() -> new CustomException(ErrorCode.CHARACTER_NOT_FOUND));

    }

    private User findCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        return userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
}
