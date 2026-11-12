package com.capstone.fertility.domain.test.support;

import com.capstone.fertility.domain.user.enums.Gender;

/**
 * 연령·성별 또래 평균(BMI·수면) — interim-report와 상세 리포트 comparisonTable 공통.
 */
public final class HealthBenchmarkSupport {

    private static final Double[] AGE_DECADE_MEAN_BMI_MALE = {
            24.08, 25.68, 25.70, 25.11, 24.89
    };
    private static final Double[] AGE_DECADE_MEAN_BMI_FEMALE = {
            21.54, 21.94, 23.13, 23.55, 24.02
    };

    private HealthBenchmarkSupport() {
    }

    public record SleepPeerBenchmark(String ageBandLabel, double avgHours) {}

    public static double bmi(double heightCm, double weightKg) {
        double heightM = heightCm / 100.0;
        return weightKg / (heightM * heightM);
    }

    public static double round1(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    /** 만 20~69세·성별 평균 BMI. 해당 없으면 null. */
    public static Double meanBmi(Gender gender, int age) {
        int idx = ageDecadeIndex(age);
        if (idx < 0 || gender == null) {
            return null;
        }
        if (gender == Gender.F) {
            return valueAtBand(AGE_DECADE_MEAN_BMI_FEMALE, idx);
        }
        if (gender == Gender.M) {
            return valueAtBand(AGE_DECADE_MEAN_BMI_MALE, idx);
        }
        return null;
    }

    /** 만 20세 이상 수면 또래 평균(시간). 만 20 미만이면 null. */
    public static SleepPeerBenchmark sleepPeerBenchmark(int age) {
        if (age < 20) {
            return null;
        }
        if (age <= 29) {
            return new SleepPeerBenchmark("20대", 8.0 + 17.0 / 60.0);
        }
        if (age <= 39) {
            return new SleepPeerBenchmark("30대", 8.0 + 7.0 / 60.0);
        }
        if (age <= 49) {
            return new SleepPeerBenchmark("40대", 7.0 + 54.0 / 60.0);
        }
        if (age <= 59) {
            return new SleepPeerBenchmark("50대", 7.0 + 42.0 / 60.0);
        }
        return new SleepPeerBenchmark("60대 이상", 8.0 + 5.0 / 60.0);
    }

    private static int ageDecadeIndex(int age) {
        if (age >= 20 && age <= 29) return 0;
        if (age >= 30 && age <= 39) return 1;
        if (age >= 40 && age <= 49) return 2;
        if (age >= 50 && age <= 59) return 3;
        if (age >= 60 && age <= 69) return 4;
        return -1;
    }

    private static Double valueAtBand(Double[] row, int idx) {
        if (row == null || idx < 0 || idx >= row.length) {
            return null;
        }
        return row[idx];
    }
}
