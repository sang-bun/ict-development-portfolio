package com.capstone.fertility.domain.report.support;

import com.capstone.fertility.domain.report.dto.res.ReportResDTO;
import com.capstone.fertility.domain.test.entity.TestSession;
import com.capstone.fertility.domain.test.support.HealthBenchmarkSupport;
import com.capstone.fertility.domain.test.support.SleepInputSupport;
import com.capstone.fertility.domain.user.enums.Gender;

import java.util.ArrayList;
import java.util.List;

/**
 * 상세 리포트 {@code comparisonTable} — 또래(BMI·수면) 대비. 프론트 item 고정명 사용.
 */
public final class ReportComparisonTableSupport {

    public static final String ITEM_BMI = "BMI";
    public static final String ITEM_SLEEP = "수면시간";

    private static final double NEUTRAL_PCT_THRESHOLD = 3.0;

    private ReportComparisonTableSupport() {
    }

    public static List<ReportResDTO.ComparisonRow> buildFrom(TestSession session) {
        if (session == null) {
            return List.of();
        }
        List<ReportResDTO.ComparisonRow> rows = new ArrayList<>();
        addBmiRow(rows, session);
        addSleepRow(rows, session);
        return List.copyOf(rows);
    }

    private static void addBmiRow(List<ReportResDTO.ComparisonRow> rows, TestSession session) {
        Integer age = session.getAge();
        Gender gender = session.getGender();
        Double height = session.getHeight();
        Double weight = session.getWeight();
        if (age == null || gender == null || height == null || weight == null) {
            return;
        }
        Double peerBmi = HealthBenchmarkSupport.meanBmi(gender, age);
        if (peerBmi == null || peerBmi <= 0) {
            return;
        }
        double myBmi = HealthBenchmarkSupport.bmi(height, weight);
        ComparisonCopy copy = comparisonCopy(myBmi, peerBmi);
        rows.add(ReportResDTO.ComparisonRow.builder()
                .item(ITEM_BMI)
                .myValue(formatBmi(myBmi))
                .averageValue(formatBmi(peerBmi))
                .comparisonResult(copy.message())
                .trend(copy.trend())
                .build());
    }

    private static void addSleepRow(List<ReportResDTO.ComparisonRow> rows, TestSession session) {
        Integer age = session.getAge();
        Integer sleepHours = session.getSleepHours();
        Integer sleepMinutes = session.getSleepMinutes();
        if (age == null || sleepHours == null || sleepMinutes == null) {
            return;
        }
        HealthBenchmarkSupport.SleepPeerBenchmark peer = HealthBenchmarkSupport.sleepPeerBenchmark(age);
        if (peer == null || peer.avgHours() <= 0) {
            return;
        }
        double myHours = SleepInputSupport.totalSleepHoursDecimal(sleepHours, sleepMinutes);
        ComparisonCopy copy = comparisonCopy(myHours, peer.avgHours());
        rows.add(ReportResDTO.ComparisonRow.builder()
                .item(ITEM_SLEEP)
                .myValue(formatSleepHours(myHours))
                .averageValue(formatSleepHours(peer.avgHours()))
                .comparisonResult(copy.message())
                .trend(copy.trend())
                .build());
    }

    private static String formatBmi(double bmi) {
        return String.valueOf(HealthBenchmarkSupport.round1(bmi));
    }

    private static String formatSleepHours(double hours) {
        int whole = (int) Math.floor(hours);
        int minutes = (int) Math.round((hours - whole) * 60);
        if (minutes == 0) {
            return whole + "시간";
        }
        if (minutes == 60) {
            return (whole + 1) + "시간";
        }
        return whole + "시간 " + minutes + "분";
    }

    private static ComparisonCopy comparisonCopy(double myValue, double averageValue) {
        double pct = Math.abs((myValue - averageValue) / averageValue * 100.0);
        double roundedPct = HealthBenchmarkSupport.round1(pct);
        if (pct < NEUTRAL_PCT_THRESHOLD) {
            return new ComparisonCopy("또래 평균과 비슷합니다", "neutral");
        }
        if (myValue > averageValue) {
            return new ComparisonCopy(roundedPct + "% 더 높습니다", "higher");
        }
        return new ComparisonCopy(roundedPct + "% 더 낮습니다", "lower");
    }

    private record ComparisonCopy(String message, String trend) {}
}
