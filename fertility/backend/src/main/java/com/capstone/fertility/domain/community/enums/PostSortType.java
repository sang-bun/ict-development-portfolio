package com.capstone.fertility.domain.community.enums;

public enum PostSortType {
    LATEST,
    POPULAR,
    COMMENTS;

    public static PostSortType fromQuery(String sort) {
        if (sort == null || sort.isBlank()) {
            return LATEST;
        }
        return switch (sort.trim().toLowerCase()) {
            case "popular" -> POPULAR;
            case "comments" -> COMMENTS;
            default -> LATEST;
        };
    }
}
