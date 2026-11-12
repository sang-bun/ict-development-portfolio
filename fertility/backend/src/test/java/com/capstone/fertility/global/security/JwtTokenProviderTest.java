package com.capstone.fertility.global.security;

import com.capstone.fertility.domain.user.enums.Role;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtTokenProviderTest {

    @Test
    void createToken_failsWhenSecretTooShort() {
        assertThatThrownBy(() -> new JwtTokenProvider("short-secret", 3_600_000L))
                .isInstanceOf(io.jsonwebtoken.security.WeakKeyException.class);
    }

    @Test
    void createToken_succeedsWithLongEnoughSecret() {
        JwtTokenProvider provider = new JwtTokenProvider(
                "local-jwt-secret-key-for-development-environment",
                3_600_000L
        );

        String token = provider.createToken(1L, Role.USER);

        assertThat(token).isNotBlank();
        assertThat(provider.validateToken(token)).isTrue();
    }
}
