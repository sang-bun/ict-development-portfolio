package com.capstone.fertility.domain.wellnessmission.enums;

public enum MissionCategory {
    SMOKING,
    DRINKING,
    SLEEP,
    EXERCISE,
    DISEASE,
    AGE,
    WEIGHT,
    OTHER;

    public static MissionCategory parseOrOther(String raw) {
        if (raw == null) return OTHER;
        try {
            return MissionCategory.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return OTHER;
        }
    }
}
