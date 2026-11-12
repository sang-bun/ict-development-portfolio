package com.capstone.fertility.domain.wellnessmission.entity;

import com.capstone.fertility.domain.result.entity.TestResult;
import com.capstone.fertility.domain.user.entity.User;
import com.capstone.fertility.domain.wellnessmission.enums.Difficulty;
import com.capstone.fertility.domain.wellnessmission.enums.FrequencyType;
import com.capstone.fertility.domain.wellnessmission.enums.MissionCategory;
import com.capstone.fertility.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "wellness_missions",
        indexes = {
                @Index(name = "idx_wellness_missions_user", columnList = "user_id"),
                @Index(name = "idx_wellness_missions_result", columnList = "test_result_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class WellnessMission extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wellness_mission_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "test_result_id", nullable = false)
    private TestResult testResult;

    @Column(name = "title", length = 200, nullable = false)
    private String title;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "linked_factor", length = 200)
    private String linkedFactor;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", length = 30, nullable = false)
    private MissionCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "frequency_type", length = 20, nullable = false)
    private FrequencyType frequencyType;

    @Column(name = "frequency_count", nullable = false)
    private Integer frequencyCount;

    @Column(name = "frequency_unit", length = 20)
    private String frequencyUnit;

    @Column(name = "duration_value")
    private Integer durationValue;

    @Column(name = "duration_unit", length = 20)
    private String durationUnit;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty", length = 20, nullable = false)
    private Difficulty difficulty;

    @Builder.Default
    @Column(name = "user_adjustable", nullable = false)
    private boolean userAdjustable = true;

    @Builder.Default
    @Column(name = "user_adjusted", nullable = false)
    private boolean userAdjusted = false;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    /**
     * KST 기준 이 미션이 속한 '오늘의 일괄' 날짜. 자정 이후에는 복제된 새 행이 오늘 날짜를 갖는다.
     */
    @Column(name = "serving_local_date")
    private LocalDate servingLocalDate;

    public void adjust(Integer frequencyCount, Integer durationValue, Difficulty difficulty) {
        if (frequencyCount != null) {
            this.frequencyCount = frequencyCount;
        }
        if (durationValue != null) {
            this.durationValue = durationValue;
        }
        if (difficulty != null) {
            this.difficulty = difficulty;
        }
        this.userAdjusted = true;
    }

    public boolean isCompleted() {
        return this.completedAt != null;
    }

    public void markCompleted(LocalDateTime when) {
        this.completedAt = when;
    }
}
