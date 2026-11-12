package umc.fitty.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.fitty.domain.User;
import umc.fitty.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String userIdAsString) throws UsernameNotFoundException {
        Long userId;
        try {
            userId = Long.valueOf(userIdAsString);
        } catch (NumberFormatException e) {
            throw new UsernameNotFoundException("Invalid user id in token: " + userIdAsString);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userId));

        if (user.isDeleted()) {
            throw new DisabledException("탈퇴한 사용자입니다.");
        }

        return UserPrincipal.from(user);
    }

    /** 필요하면 서비스 내부에서 직접 id로 로드하는 전용 메서드 */
    @Transactional(readOnly = true)
    public UserDetails loadUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userId));
        return UserPrincipal.from(user);
    }
}
