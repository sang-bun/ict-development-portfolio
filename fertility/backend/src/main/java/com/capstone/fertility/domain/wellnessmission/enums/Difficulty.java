package com.capstone.fertility.domain.wellnessmission.enums;

public enum Difficulty {
    EASY,
    MEDIUM,
    HARD;

    public static Difficulty parseOrMedium(String raw) {
        if (raw == null) return MEDIUM;
        try {
            return Difficulty.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return MEDIUM;
        }
    }
}
