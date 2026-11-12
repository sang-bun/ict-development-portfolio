package com.capstone.fertility.domain.wellnessmission.entity;

import com.capstone.fertility.domain.user.entity.User;
import com.capstone.fertility.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "wellness_mission_cycle_completions",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_wm_cycle_completion_user_mission_cycle",
                        columnNames = {"user_id", "wellness_mission_id", "cycle_index"}
                )
        },
        indexes = {
                @Index(name = "idx_wm_cycle_completion_user_result_cycle", columnList = "user_id, test_result_id, cycle_index")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class WellnessMissionCycleCompletion extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wellness_mission_cycle_completion_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "wellness_mission_id", nullable = false)
    private WellnessMission wellnessMission;

    /** 조회 편의용 (미션 엔티티 로딩 없이 집계) */
    @Column(name = "test_result_id", nullable = false)
    private Long testResultId;

    @Column(name = "cycle_index", nullable = false)
    private int cycleIndex;

    @Column(name = "completed_at", nullable = false)
    private LocalDateTime completedAt;
}
