package com.capstone.fertility.domain.report.support;

import com.capstone.fertility.domain.report.dto.res.ReportResDTO;
import com.capstone.fertility.domain.test.entity.TestSession;
import com.capstone.fertility.domain.user.enums.Gender;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class ReportQuestionnaireSupportTest {

    @Test
    void buildFrom_maleSession_splitsBodyAndLifestyleGroups() {
        TestSession session = TestSession.builder()
                .gender(Gender.M)
                .age(32)
                .height(175.0)
                .weight(70.0)
                .smokeStatus("OCCASIONAL")
                .drinkStatus("MONTHLY_1_TO_3")
                .sleepHours(6)
                .sleepMinutes(12)
                .build();

        Map<String, Map<String, String>> groups = groupMap(ReportQuestionnaireSupport.buildFrom(session));

        assertThat(groups).containsKey(ReportQuestionnaireSupport.GROUP_BODY);
        assertThat(groups.get(ReportQuestionnaireSupport.GROUP_BODY))
                .containsEntry(ReportQuestionnaireSupport.LABEL_AGE, "32세")
                .containsEntry(ReportQuestionnaireSupport.LABEL_HEIGHT, "175cm")
                .containsEntry(ReportQuestionnaireSupport.LABEL_WEIGHT, "70kg");

        assertThat(groups).containsKey(ReportQuestionnaireSupport.GROUP_LIFESTYLE);
        assertThat(groups.get(ReportQuestionnaireSupport.GROUP_LIFESTYLE))
                .containsEntry(ReportQuestionnaireSupport.LABEL_SMOKE, "가끔")
                .containsEntry(ReportQuestionnaireSupport.LABEL_DRINK, "월 1~3회")
                .containsEntry(ReportQuestionnaireSupport.LABEL_SLEEP, "6시간 12분");
    }

    @Test
    void buildFrom_femaleSession_rawCigarettes_showsCountLabel() {
        TestSession session = TestSession.builder()
                .gender(Gender.F)
                .age(36)
                .smokeLevel(2)
                .cigarettesPerDay(10)
                .build();

        Map<String, Map<String, String>> groups = groupMap(ReportQuestionnaireSupport.buildFrom(session));

        assertThat(groups.get(ReportQuestionnaireSupport.GROUP_LIFESTYLE))
                .containsEntry(ReportQuestionnaireSupport.LABEL_SMOKE, "하루 10개비");
    }

    @Test
    void buildFrom_femaleSession_showsDrinkWhenDrinkStatusStored() {
        TestSession session = TestSession.builder()
                .gender(Gender.F)
                .drinkStatus("WEEKLY_OR_MORE")
                .bingeDaysPerYear(100)
                .binge12(2)
                .build();

        Map<String, Map<String, String>> groups = groupMap(ReportQuestionnaireSupport.buildFrom(session));

        assertThat(groups.get(ReportQuestionnaireSupport.GROUP_LIFESTYLE))
                .containsEntry(ReportQuestionnaireSupport.LABEL_DRINK, "주 1회 이상")
                .containsEntry(ReportQuestionnaireSupport.LABEL_BINGE, "최근 1년 폭음(5잔+) 100일");
    }

    @Test
    void buildFrom_femaleSession_mapsSmokeLevelToShortLabel() {
        TestSession session = TestSession.builder()
                .gender(Gender.F)
                .age(28)
                .height(162.0)
                .weight(55.0)
                .smokeLevel(2)
                .build();

        Map<String, Map<String, String>> groups = groupMap(ReportQuestionnaireSupport.buildFrom(session));

        assertThat(groups.get(ReportQuestionnaireSupport.GROUP_LIFESTYLE))
                .containsEntry(ReportQuestionnaireSupport.LABEL_SMOKE, "매일");
        assertThat(groups.get(ReportQuestionnaireSupport.GROUP_LIFESTYLE))
                .containsEntry(ReportQuestionnaireSupport.LABEL_DRINK, ReportQuestionnaireSupport.VALUE_NON_DRINKER);
    }

    @Test
    void buildFrom_maleSession_showsNonDrinkerWhenDrinkMissing() {
        TestSession session = TestSession.builder()
                .gender(Gender.M)
                .age(30)
                .drinkStatus(null)
                .smokeStatus("NEVER")
                .build();

        Map<String, Map<String, String>> groups = groupMap(ReportQuestionnaireSupport.buildFrom(session));

        assertThat(groups.get(ReportQuestionnaireSupport.GROUP_LIFESTYLE))
                .containsEntry(ReportQuestionnaireSupport.LABEL_DRINK, ReportQuestionnaireSupport.VALUE_NON_DRINKER);
    }

    @Test
    void buildFrom_maleSession_neverMapsToNonDrinkerLabel() {
        TestSession session = TestSession.builder()
                .gender(Gender.M)
                .drinkStatus("NEVER")
                .build();

        Map<String, Map<String, String>> groups = groupMap(ReportQuestionnaireSupport.buildFrom(session));

        assertThat(groups.get(ReportQuestionnaireSupport.GROUP_LIFESTYLE))
                .containsEntry(ReportQuestionnaireSupport.LABEL_DRINK, ReportQuestionnaireSupport.VALUE_NON_DRINKER);
    }

    private static Map<String, Map<String, String>> groupMap(List<ReportResDTO.QuestionnaireGroup> groups) {
        return groups.stream()
                .collect(Collectors.toMap(
                        ReportResDTO.QuestionnaireGroup::title,
                        g -> g.rows().stream()
                                .collect(Collectors.toMap(
                                        ReportResDTO.QuestionnaireRow::label,
                                        ReportResDTO.QuestionnaireRow::value))
                ));
    }
}
