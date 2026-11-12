package com.capstone.fertility.global.ai.mapping;

/**
 * 프론트 설문 API 코드(NEVER, MONTHLY_1_TO_3 등) 및 한국어 라벨을
 * AI 0~2 스케일·리포트 표시용 한글로 변환합니다.
 * <p>
 * 여성: smokeLevel/binge12에 0~2 tier 또는 설문 원시값(개비·연간 폭음 일수) 수용.
 * AI 전달 시 원시값 우선, 없으면 tier 대표값(3개비, 6일, 10개비, 13일).
 */
public final class AiLifestyleCategoryMapper {

    private AiLifestyleCategoryMapper() {
    }

    /** 흡연 표시용 한글 라벨 */
    public static String toSmokeDisplayLabel(String smokeStatus) {
        if (smokeStatus == null || smokeStatus.isBlank()) {
            return null;
        }
        return switch (normalizeToken(smokeStatus)) {
            case "NEVER", "NONE" -> "안 피움";
            case "OCCASIONAL", "OCCASIONALLY", "SOMETIMES" -> "가끔 피움";
            case "DAILY" -> "매일 피움";
            case "안 피움" -> "안 피움";
            case "가끔 피움" -> "가끔 피움";
            case "매일 피움" -> "매일 피움";
            default -> smokeStatus.trim();
        };
    }

    public static int mapSmokeStatusToAi(String smokeStatus) {
        if (smokeStatus == null || smokeStatus.isBlank()) {
            return 0;
        }
        return switch (normalizeToken(smokeStatus)) {
            case "NEVER", "NONE", "안 피움" -> 0;
            case "OCCASIONAL", "OCCASIONALLY", "SOMETIMES", "가끔 피움" -> 1;
            case "DAILY", "매일 피움" -> 2;
            default -> 0;
        };
    }

    /** 음주 표시용 한글 라벨 */
    public static String toDrinkDisplayLabel(String drinkStatus) {
        if (drinkStatus == null || drinkStatus.isBlank()) {
            return null;
        }
        return switch (normalizeToken(drinkStatus)) {
            case "NEVER", "NONE" -> "안 마심";
            case "MONTHLY_1_TO_3", "SOMETIMES" -> "월 1~3회";
            case "WEEKLY_OR_MORE", "WEEKLY", "WEEKLY_1_OR_MORE" -> "주 1회 이상";
            case "안 마심" -> "안 마심";
            case "월 1~3회" -> "월 1~3회";
            case "주 1회 이상" -> "주 1회 이상";
            default -> drinkStatus.trim();
        };
    }

    public static int mapDrinkStatusToAi(String drinkStatus) {
        if (drinkStatus == null || drinkStatus.isBlank()) {
            return 0;
        }
        return switch (normalizeToken(drinkStatus)) {
            case "NEVER", "NONE", "안 마심" -> 0;
            case "MONTHLY_1_TO_3", "SOMETIMES", "월 1~3회" -> 1;
            case "WEEKLY_OR_MORE", "WEEKLY", "WEEKLY_1_OR_MORE", "주 1회 이상" -> 2;
            default -> 0;
        };
    }

    /** 폭음 표시용 한글 라벨 */
    public static String toBingeDisplayLabel(String bingeStatus) {
        if (bingeStatus == null || bingeStatus.isBlank()) {
            return null;
        }
        return switch (normalizeToken(bingeStatus)) {
            case "NEVER", "NONE" -> "없음";
            case "MONTHLY_1", "MONTHLY" -> "월 1회";
            case "WEEKLY_OR_MORE", "WEEKLY", "WEEKLY_1_OR_MORE" -> "주 1회 이상";
            case "없음" -> "없음";
            case "월 1회" -> "월 1회";
            case "주 1회 이상" -> "주 1회 이상";
            default -> bingeStatus.trim();
        };
    }

    public static int mapBingeStatusToAi(String bingeStatus) {
        if (bingeStatus == null || bingeStatus.isBlank()) {
            return 0;
        }
        return switch (normalizeToken(bingeStatus)) {
            case "NEVER", "NONE", "없음" -> 0;
            case "MONTHLY_1", "MONTHLY", "월 1회" -> 1;
            case "WEEKLY_OR_MORE", "WEEKLY", "WEEKLY_1_OR_MORE", "주 1회 이상" -> 2;
            default -> 0;
        };
    }

    private static String normalizeToken(String raw) {
        return raw.trim().toUpperCase();
    }

    /** 하루 개비 수 → tier: 0→0, 1~5→1, 6+→2 */
    public static int tierFromCigarettesPerDay(int cigarettesPerDay) {
        if (cigarettesPerDay <= 0) {
            return 0;
        }
        if (cigarettesPerDay <= 5) {
            return 1;
        }
        return 2;
    }

    /** 연간 폭음(5잔+) 일수 → tier: 0→0, 1~12→1, 13+→2 */
    /**
     * 여성 음주: {@code drinkStatus}(한글·API 코드·"0"|"1"|"2") 또는 {@code drinkLevel}(0~2) → DB 저장용 API 코드.
     * 프론트가 drinkStatus 없이 drinkLevel만 보내는 경우 대응.
     */
    public static String resolveFemaleDrinkStatus(String drinkStatus, Integer drinkLevel) {
        if (drinkStatus != null && !drinkStatus.isBlank()) {
            String token = normalizeToken(drinkStatus);
            return switch (token) {
                case "NEVER", "NONE", "안 마심", "0" -> "NEVER";
                case "MONTHLY_1_TO_3", "SOMETIMES", "월 1~3회", "1" -> "MONTHLY_1_TO_3";
                case "WEEKLY_OR_MORE", "WEEKLY", "WEEKLY_1_OR_MORE", "주 1회 이상", "2" -> "WEEKLY_OR_MORE";
                default -> {
                    try {
                        int n = Integer.parseInt(token);
                        if (n >= 0 && n <= 2) {
                            yield resolveFemaleDrinkStatus(null, n);
                        }
                    } catch (NumberFormatException ignored) {
                        // fall through
                    }
                    yield drinkStatus.trim();
                }
            };
        }
        if (drinkLevel == null) {
            return null;
        }
        int level = drinkLevel;
        if (level >= 0 && level <= 2) {
            return switch (level) {
                case 0 -> "NEVER";
                case 1 -> "MONTHLY_1_TO_3";
                case 2 -> "WEEKLY_OR_MORE";
                default -> null;
            };
        }
        return switch (level) {
            case 1 -> "NEVER";
            case 3 -> "MONTHLY_1_TO_3";
            case 5 -> "WEEKLY_OR_MORE";
            default -> null;
        };
    }

    public static int tierFromBingeDaysPerYear(int bingeDaysPerYear) {
        if (bingeDaysPerYear <= 0) {
            return 0;
        }
        if (bingeDaysPerYear <= 12) {
            return 1;
        }
        return 2;
    }

    public static FemaleLifestyleNormalized normalizeFemaleSmokeInput(
            Integer smokeLevelOrRaw,
            Integer cigarettesPerDay
    ) {
        if (cigarettesPerDay != null) {
            int raw = Math.max(0, cigarettesPerDay);
            return new FemaleLifestyleNormalized(tierFromCigarettesPerDay(raw), raw);
        }
        if (smokeLevelOrRaw == null) {
            return null;
        }
        int v = smokeLevelOrRaw;
        if (v >= 0 && v <= 2) {
            return new FemaleLifestyleNormalized(v, null);
        }
        return switch (v) {
            case 1 -> new FemaleLifestyleNormalized(0, null);
            case 3 -> new FemaleLifestyleNormalized(1, null);
            case 5 -> new FemaleLifestyleNormalized(2, null);
            default -> {
                int raw = Math.max(0, v);
                yield new FemaleLifestyleNormalized(tierFromCigarettesPerDay(raw), raw);
            }
        };
    }

    public static FemaleLifestyleNormalized normalizeFemaleBingeInput(
            Integer binge12OrRaw,
            Integer bingeDaysPerYear
    ) {
        if (bingeDaysPerYear != null) {
            int raw = Math.max(0, bingeDaysPerYear);
            return new FemaleLifestyleNormalized(tierFromBingeDaysPerYear(raw), raw);
        }
        if (binge12OrRaw == null) {
            return null;
        }
        int v = binge12OrRaw;
        if (v >= 0 && v <= 2) {
            return new FemaleLifestyleNormalized(v, null);
        }
        return switch (v) {
            case 1 -> new FemaleLifestyleNormalized(0, null);
            case 3 -> new FemaleLifestyleNormalized(1, null);
            case 5 -> new FemaleLifestyleNormalized(2, null);
            default -> {
                int raw = Math.max(0, v);
                yield new FemaleLifestyleNormalized(tierFromBingeDaysPerYear(raw), raw);
            }
        };
    }

    public static int femaleSmokeForAi(Integer smokeTier, Integer cigarettesPerDay) {
        if (cigarettesPerDay != null) {
            return Math.max(0, cigarettesPerDay);
        }
        if (smokeTier == null) {
            return 0;
        }
        return switch (smokeTier) {
            case 0 -> 0;
            case 1 -> 3;
            case 2 -> 10;
            default -> 0;
        };
    }

    public static int femaleBingeForAi(Integer bingeTier, Integer bingeDaysPerYear) {
        if (bingeDaysPerYear != null) {
            return Math.max(0, bingeDaysPerYear);
        }
        if (bingeTier == null) {
            return 0;
        }
        return switch (bingeTier) {
            case 0 -> 0;
            case 1 -> 6;
            case 2 -> 13;
            default -> 0;
        };
    }

    /** @deprecated {@link #femaleSmokeForAi(Integer, Integer)} 사용 */
    @Deprecated
    public static int mapFemaleSmokeLevelToAi(Integer smokeLevel) {
        return femaleSmokeForAi(smokeLevel, null);
    }

    /** @deprecated {@link #femaleBingeForAi(Integer, Integer)} 사용 */
    @Deprecated
    public static int mapFemaleBingeLevelToAi(Integer binge12) {
        return femaleBingeForAi(binge12, null);
    }
}
