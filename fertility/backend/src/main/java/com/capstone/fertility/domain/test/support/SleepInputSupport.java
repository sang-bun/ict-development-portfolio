package com.capstone.fertility.domain.test.support;

import com.capstone.fertility.domain.test.exception.TestException;
import com.capstone.fertility.domain.test.exception.code.TestErrorCode;

/**
 * 수면 시·분 입력 검증 및 시간(소수) 환산.
 */
public final class SleepInputSupport {

    public static final int MAX_TOTAL_MINUTES = 24 * 60;

    private SleepInputSupport() {
    }

    /**
     * 둘 다 null이면 통과. 하나만 오면 예외. 둘 다 있으면 범위·합계(≤24h) 검증.
     */
    public static void validateOptionalPair(Integer hours, Integer minutes) {
        if (hours == null && minutes == null) {
            return;
        }
        if (hours == null || minutes == null) {
            throw new TestException(TestErrorCode.INVALID_SLEEP);
        }
        validateNonNullPair(hours, minutes);
    }

    /**
     * 최종 제출 등, 수면이 반드시 있어야 할 때 사용.
     */
    public static void validateRequiredPair(Integer hours, Integer minutes) {
        if (hours == null || minutes == null) {
            throw new TestException(TestErrorCode.INVALID_SLEEP);
        }
        validateNonNullPair(hours, minutes);
    }

    private static void validateNonNullPair(int hours, int minutes) {
        if (minutes < 0 || minutes > 59) {
            throw new TestException(TestErrorCode.INVALID_SLEEP);
        }
        if (hours < 0 || hours > 24) {
            throw new TestException(TestErrorCode.INVALID_SLEEP);
        }
        if (hours == 24 && minutes != 0) {
            throw new TestException(TestErrorCode.INVALID_SLEEP);
        }
        int total = hours * 60 + minutes;
        if (total > MAX_TOTAL_MINUTES) {
            throw new TestException(TestErrorCode.INVALID_SLEEP);
        }
    }

    public static double totalSleepHoursDecimal(int hours, int minutes) {
        return hours + minutes / 60.0;
    }

    public static Double totalSleepHoursDecimalOrNull(Integer hours, Integer minutes) {
        if (hours == null || minutes == null) {
            return null;
        }
        return totalSleepHoursDecimal(hours, minutes);
    }

    /**
     * AI JSON 등에 넣을 때 과도한 소수 자릿수 줄이기.
     */
    public static double roundSleepHoursForAi(int hours, int minutes) {
        double raw = totalSleepHoursDecimal(hours, minutes);
        return Math.round(raw * 100.0) / 100.0;
    }

    public static String formatKorean(Integer hours, Integer minutes) {
        if (hours == null || minutes == null) {
            return "정보 없음";
        }
        if (minutes == 0) {
            return hours + "시간";
        }
        return hours + "시간 " + minutes + "분";
    }
}
