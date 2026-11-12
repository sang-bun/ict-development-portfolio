package com.capstone.fertility.domain.wellnessmission.enums;

public enum FrequencyType {
    DAILY,
    WEEKLY;

    public static FrequencyType parseOrDaily(String raw) {
        if (raw == null) return DAILY;
        try {
            return FrequencyType.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return DAILY;
        }
    }
}
