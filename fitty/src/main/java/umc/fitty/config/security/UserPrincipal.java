package umc.fitty.config.security;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import umc.fitty.domain.User;

import java.util.Collection;
import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserPrincipal implements UserDetails {

    private final Long id;               // 내부 PK
    private final String nickname;
    private final String role;           // "ROLE_USER" 기본

    public static UserPrincipal from(User user) {
        String role = "ROLE_USER";
        return new UserPrincipal(
                user.getId(),
                user.getNickname(),
                role
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }

    // 소셜 로그인이라 패스워드 사용 안 함
    @Override public String getPassword() { return ""; }
    // username은 식별 용도로만 사용 → id 문자열 반환
    @Override public String getUsername() { return String.valueOf(id); }

    @Override public boolean isAccountNonExpired()     { return true; }
    @Override public boolean isAccountNonLocked()      { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled()               { return true; }
}
