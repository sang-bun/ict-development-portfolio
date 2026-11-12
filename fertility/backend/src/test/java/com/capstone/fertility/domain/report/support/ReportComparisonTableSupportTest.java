package com.capstone.fertility.domain.report.support;

import com.capstone.fertility.domain.report.dto.res.ReportResDTO;
import com.capstone.fertility.domain.test.entity.TestSession;
import com.capstone.fertility.domain.user.enums.Gender;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class ReportComparisonTableSupportTest {

    @Test
    void buildFrom_includesBmiAndSleepRows() {
        TestSession session = TestSession.builder()
                .gender(Gender.M)
                .age(32)
                .height(175.0)
                .weight(80.0)
                .sleepHours(6)
                .sleepMinutes(0)
                .build();

        Map<String, ReportResDTO.ComparisonRow> rows = ReportComparisonTableSupport.buildFrom(session).stream()
                .collect(Collectors.toMap(ReportResDTO.ComparisonRow::item, Function.identity()));

        assertThat(rows).containsKey(ReportComparisonTableSupport.ITEM_BMI);
        assertThat(rows.get(ReportComparisonTableSupport.ITEM_BMI).trend()).isIn("higher", "lower", "neutral");
        assertThat(rows.get(ReportComparisonTableSupport.ITEM_BMI).comparisonResult()).isNotBlank();

        assertThat(rows).containsKey(ReportComparisonTableSupport.ITEM_SLEEP);
        assertThat(rows.get(ReportComparisonTableSupport.ITEM_SLEEP).myValue()).isEqualTo("6시간");
        assertThat(rows.get(ReportComparisonTableSupport.ITEM_SLEEP).averageValue()).isNotBlank();
    }
}
