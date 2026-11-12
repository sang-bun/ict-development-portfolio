package com.capstone.fertility.domain.community.support;

import com.capstone.fertility.domain.community.enums.PostCategory;
import com.capstone.fertility.domain.community.exception.CommunityException;
import com.capstone.fertility.domain.community.exception.code.CommunityErrorCode;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;

public final class CommunityValidationSupport {

    private static final int TITLE_MAX = 50;
    private static final int BODY_MAX = 4000;
    private static final int TAG_MAX_COUNT = 8;
    private static final int TAG_MAX_LEN = 20;
    private static final int IMAGE_MAX = 5;
    private static final int COMMENT_BODY_MAX = 1000;

    private CommunityValidationSupport() {
    }

    public static PostCategory parseCategory(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new CommunityException(CommunityErrorCode.INVALID_CATEGORY);
        }
        try {
            return PostCategory.valueOf(raw.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new CommunityException(CommunityErrorCode.INVALID_CATEGORY);
        }
    }

    public static String requireTitle(String title) {
        if (title == null) {
            throw new CommunityException(CommunityErrorCode.INVALID_REQUEST);
        }
        String trimmed = title.trim();
        if (trimmed.isEmpty() || trimmed.length() > TITLE_MAX) {
            throw new CommunityException(CommunityErrorCode.INVALID_REQUEST);
        }
        return trimmed;
    }

    public static String requireBody(String body) {
        if (body == null) {
            throw new CommunityException(CommunityErrorCode.INVALID_REQUEST);
        }
        String trimmed = body.trim();
        if (trimmed.isEmpty() || trimmed.length() > BODY_MAX) {
            throw new CommunityException(CommunityErrorCode.INVALID_REQUEST);
        }
        return trimmed;
    }

    public static String requireCommentBody(String body) {
        if (body == null) {
            throw new CommunityException(CommunityErrorCode.INVALID_REQUEST);
        }
        String trimmed = body.trim();
        if (trimmed.isEmpty() || trimmed.length() > COMMENT_BODY_MAX) {
            throw new CommunityException(CommunityErrorCode.INVALID_REQUEST);
        }
        return trimmed;
    }

    public static List<String> normalizeTags(List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return List.of();
        }
        LinkedHashSet<String> normalized = new LinkedHashSet<>();
        for (String tag : tags) {
            if (tag == null) {
                continue;
            }
            String trimmed = tag.trim();
            if (trimmed.isEmpty() || trimmed.length() > TAG_MAX_LEN) {
                throw new CommunityException(CommunityErrorCode.INVALID_REQUEST);
            }
            normalized.add(trimmed);
            if (normalized.size() > TAG_MAX_COUNT) {
                throw new CommunityException(CommunityErrorCode.INVALID_REQUEST);
            }
        }
        return new ArrayList<>(normalized);
    }

    public static List<String> normalizeImageUrls(List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return List.of();
        }
        if (imageUrls.size() > IMAGE_MAX) {
            throw new CommunityException(CommunityErrorCode.INVALID_REQUEST);
        }
        List<String> result = new ArrayList<>();
        for (String url : imageUrls) {
            if (url == null || url.isBlank()) {
                throw new CommunityException(CommunityErrorCode.INVALID_REQUEST);
            }
            result.add(url.trim());
        }
        return result;
    }

    public static String bodyPreview(String body) {
        if (body == null) {
            return "";
        }
        String trimmed = body.trim();
        if (trimmed.length() <= 120) {
            return trimmed;
        }
        return trimmed.substring(0, 120) + "…";
    }
}
