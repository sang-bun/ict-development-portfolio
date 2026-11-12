package umc.fitty.domain;

import jakarta.persistence.*;
import lombok.*;
import umc.fitty.domain.common.BaseEntity;
import umc.fitty.domain.enums.GoalType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "goal_uk", columnNames = {"user_id", "weekStartDate"})
})
public class Goal extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GoalType type; // 🔸 이번 주 목표 타입

    private Double  targetDistance;
    private Double  progressDistance;

    private Integer targetCount;
    private Integer progressCount;

    private Integer targetTime;   // minutes
    private Integer progressTime;

    @Column(nullable = false)
    private LocalDate weekStartDate; // (월요일)

    @OneToMany(mappedBy = "goal", cascade = CascadeType.ALL)
    private List<RunRecord> runRecordList = new ArrayList<>();

    // --- 안전 연산 (null-safe & 음수 방지) ---
    public void updateProgress(Double distanceKm, Integer durationMin){
        if (distanceKm != null) this.progressDistance = nz(this.progressDistance) + distanceKm;
        if (durationMin != null) this.progressTime     = nz(this.progressTime)     + durationMin;
        this.progressCount = nz(this.progressCount) + 1;
    }

    public void decreaseProgress(Double distanceKm, Integer durationMin){
        if (distanceKm != null) this.progressDistance = Math.max(0, nz(this.progressDistance) - distanceKm);
        if (durationMin != null) this.progressTime     = Math.max(0, nz(this.progressTime)     - durationMin);
        this.progressCount = Math.max(0, nz(this.progressCount) - 1);
    }

    private int nz(Integer v){ return v == null ? 0 : v; }
    private double nz(Double v){ return v == null ? 0d : v; }

    // 선택: 요약 문구
    public String summary(){
        return switch (type){
            case COUNT    -> "일주일간 " + (targetCount==null?0:targetCount) + "번 달리기";
            case DISTANCE -> "일주일간 " + (targetDistance==null?0:targetDistance) + "km 달리기";
            case DURATION -> "일주일간 " + (targetTime==null?0:targetTime) + "분 달리기";
        };
    }
}
