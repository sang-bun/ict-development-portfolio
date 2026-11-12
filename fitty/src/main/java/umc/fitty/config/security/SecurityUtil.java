package umc.fitty.config.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public final class SecurityUtil {
    private SecurityUtil() {}

    public static Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("인증 정보가 없습니다.");
        }

        // 1) 이름(name)에 userId가 들어있는 경우
        try {
            return Long.valueOf(auth.getName());
        } catch (Exception ignore) { /* 다음 분기에서 재시도 */ }

        // 2) principal 타입 분기
        Object principal = auth.getPrincipal();
        if (principal instanceof UserDetails ud) {
            return Long.valueOf(ud.getUsername());
        }
        if (principal instanceof String s) {
            return Long.valueOf(s);
        }

        throw new IllegalStateException("지원하지 않는 인증 주체 타입: " + principal.getClass().getName());
    }
}
