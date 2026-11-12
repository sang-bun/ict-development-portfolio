package com.capstone.fertility.domain.user.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * 성별 enum.
 * <p>
 * DB·내부 표현은 {@code M, F} 두 값만 사용한다.
 * 다만 프론트엔드 친화성을 위해 입력 JSON에서 {@code MALE, FEMALE} 도 같은 값으로 매핑한다.
 * 출력(직렬화)은 enum.name() 그대로 {@code M / F} 로 내려간다.
 */
public enum Gender {
    M, F;

    /**
     * Jackson 역직렬화 진입점. 입력 문자열을 다음 규칙으로 매핑한다.
     * <pre>
     *   "M" / "m" / "MALE" / "male"     -> M
     *   "F" / "f" / "FEMALE" / "female" -> F
     *   그 외                            -> IllegalArgumentException (→ JSON400)
     * </pre>
     */
    @JsonCreator
    public static Gender from(String raw) {
        if (raw == null) {
            throw new IllegalArgumentException("gender must not be null");
        }
        String value = raw.trim().toUpperCase();
        return switch (value) {
            case "M", "MALE" -> M;
            case "F", "FEMALE" -> F;
            default -> throw new IllegalArgumentException("Unknown gender: " + raw + " (allowed: M, F, MALE, FEMALE)");
        };
    }
}
