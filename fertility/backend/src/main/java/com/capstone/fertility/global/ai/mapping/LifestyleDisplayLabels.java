package com.capstone.fertility.global.ai.mapping;

import com.capstone.fertility.domain.user.enums.Gender;

/**
 * 검사 세션 생활습관 필드를 프론트 표시용 한글 라벨로 변환합니다.
 */
public final class LifestyleDisplayLabels {

    private LifestyleDisplayLabels() {
    }

    public static String smokeLabel(
            Gender gender,
            String smokeStatus,
            Integer smokeLevel,
            Integer cigarettesPerDay
    ) {
        if (gender == Gender.M) {
            return labelFromMaleSmokeStatus(smokeStatus);
        }
        return labelFromFemaleSmoke(smokeLevel, cigarettesPerDay);
    }

    public static String drinkLabel(String drinkStatus) {
        if (drinkStatus == null || drinkStatus.isBlank()) {
            return "정보 없음";
        }
        return switch (drinkStatus.trim().toUpperCase()) {
            case "NEVER", "NONE", "안 마심" -> "비음주";
            case "MONTHLY_1_TO_3", "SOMETIMES", "월 1~3회" -> "월 1~3회 음주";
            case "WEEKLY_OR_MORE", "WEEKLY", "WEEKLY_1_OR_MORE", "주 1회 이상" -> "주 1회 이상 음주";
            default -> drinkStatus.trim();
        };
    }

    public static String bingeLabel(
            Gender gender,
            String bingeStatus,
            Integer bingeTier,
            Integer bingeDaysPerYear
    ) {
        if (gender == Gender.M) {
            return labelFromMaleBingeStatus(bingeStatus);
        }
        return labelFromFemaleBinge(bingeTier, bingeDaysPerYear);
    }

    private static String labelFromMaleSmokeStatus(String smokeStatus) {
        if (smokeStatus == null || smokeStatus.isBlank()) {
            return "정보 없음";
        }
        return switch (smokeStatus.trim()) {
            case "안 피움" -> "비흡연";
            case "가끔 피움" -> "가끔 흡연";
            case "매일 피움" -> "매일 흡연";
            default -> smokeStatus.trim();
        };
    }

    private static String labelFromMaleBingeStatus(String bingeStatus) {
        if (bingeStatus == null || bingeStatus.isBlank()) {
            return "정보 없음";
        }
        return switch (bingeStatus.trim()) {
            case "없음" -> "폭음 없음";
            case "월 1회" -> "월 1회 폭음";
            case "주 1회 이상" -> "주 1회 이상 폭음";
            default -> bingeStatus.trim();
        };
    }

    private static String labelFromFemaleSmoke(Integer smokeLevel, Integer cigarettesPerDay) {
        if (cigarettesPerDay != null) {
            if (cigarettesPerDay <= 0) {
                return "비흡연";
            }
            return "하루 " + cigarettesPerDay + "개비";
        }
        if (smokeLevel != null && smokeLevel > 2) {
            return labelFromFemaleSmoke(null, smokeLevel);
        }
        if (smokeLevel == null) {
            return "정보 없음";
        }
        return switch (smokeLevel) {
            case 0 -> "비흡연";
            case 1 -> "가끔 흡연 (1~5개비/일)";
            case 2 -> "흡연 (6개비/일 이상)";
            default -> "정보 없음";
        };
    }

    private static String labelFromFemaleBinge(Integer bingeTier, Integer bingeDaysPerYear) {
        if (bingeDaysPerYear != null) {
            if (bingeDaysPerYear <= 0) {
                return "폭음 없음";
            }
            return "최근 1년 폭음(5잔+) " + bingeDaysPerYear + "일";
        }
        if (bingeTier != null && bingeTier > 2) {
            return labelFromFemaleBinge(null, bingeTier);
        }
        if (bingeTier == null) {
            return "정보 없음";
        }
        return switch (bingeTier) {
            case 0 -> "폭음 없음";
            case 1 -> "가끔 폭음 (연 1~12일)";
            case 2 -> "잦은 폭음 (연 13일 이상)";
            default -> "정보 없음";
        };
    }
}
