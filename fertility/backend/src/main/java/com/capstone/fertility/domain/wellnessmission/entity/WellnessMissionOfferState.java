package com.capstone.fertility.domain.wellnessmission.entity;

import com.capstone.fertility.domain.result.entity.TestResult;
import com.capstone.fertility.domain.user.entity.User;
import com.capstone.fertility.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "wellness_mission_offer_states",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_wm_offer_user_result", columnNames = {"user_id", "test_result_id"})
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class WellnessMissionOfferState extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wellness_mission_offer_state_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "test_result_id", nullable = false)
    private TestResult testResult;

    @Column(name = "cycle_index", nullable = false)
    private int cycleIndex;

    @Column(name = "slot1_mission_id")
    private Long slot1MissionId;

    @Column(name = "slot2_mission_id")
    private Long slot2MissionId;

    @Column(name = "slot3_mission_id")
    private Long slot3MissionId;

    public void setCycleIndex(int cycleIndex) {
        this.cycleIndex = cycleIndex;
    }

    public void setSlot1MissionId(Long slot1MissionId) {
        this.slot1MissionId = slot1MissionId;
    }

    public void setSlot2MissionId(Long slot2MissionId) {
        this.slot2MissionId = slot2MissionId;
    }

    public void setSlot3MissionId(Long slot3MissionId) {
        this.slot3MissionId = slot3MissionId;
    }

    public void clearAllSlots() {
        this.slot1MissionId = null;
        this.slot2MissionId = null;
        this.slot3MissionId = null;
    }

    public void setSlotMissionIds(Long slot1, Long slot2, Long slot3) {
        this.slot1MissionId = slot1;
        this.slot2MissionId = slot2;
        this.slot3MissionId = slot3;
    }

    public boolean containsMissionId(Long missionId) {
        if (missionId == null) {
            return false;
        }
        return missionId.equals(slot1MissionId)
                || missionId.equals(slot2MissionId)
                || missionId.equals(slot3MissionId);
    }
}
