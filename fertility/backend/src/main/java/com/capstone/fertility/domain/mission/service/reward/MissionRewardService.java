package com.capstone.fertility.domain.mission.service.reward;

import com.capstone.fertility.domain.mission.entity.MissionSproutLog;
import com.capstone.fertility.domain.mission.entity.UserFlowerCollection;
import com.capstone.fertility.domain.mission.enums.FlowerType;
import com.capstone.fertility.domain.mission.enums.SproutLogAction;
import com.capstone.fertility.domain.mission.repository.MissionSproutLogRepository;
import com.capstone.fertility.domain.mission.repository.UserFlowerCollectionRepository;
import com.capstone.fertility.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 미션 완료/페널티 등으로 인한 경험치 보상을 일관되게 처리하는 도메인 서비스.
 * 정적 Mission, LLM 기반 WellnessMission 두 시스템에서 공통으로 사용한다.
 */
@Service
@RequiredArgsConstructor
public class MissionRewardService {

    /** Lv.5 도달 시 후보가 되는 꽃 풀(3종). 미보유 종 중 1개를 랜덤 지급한다. */
    private static final Set<FlowerType> FLOWER_POOL = EnumSet.of(
            FlowerType.PEONY,
            FlowerType.BABYS_BREATH,
            FlowerType.LOTUS
    );

    private final MissionSproutLogRepository missionSproutLogRepository;
    private final UserFlowerCollectionRepository userFlowerCollectionRepository;

    /**
     * 미션 완료 보상 적용.
     *
     * @param expReward 0이면 EXP·레벨 변화 없이 마지막 미션 시각·로그만 반영(일일 상한 등).
     */
    public RewardResult grantMissionCompletion(User user, int expReward) {
        boolean leveledUp = expReward != 0 && user.addExp(expReward);
        user.updateLastMissionDate();

        missionSproutLogRepository.save(MissionSproutLog.builder()
                .user(user)
                .action(SproutLogAction.MISSION_COMPLETE)
                .expDelta(expReward)
                .build());

        FlowerType awarded = null;
        if (leveledUp && user.getCurrentLevel() >= 5) {
            awarded = tryAwardFlower(user);
        }

        return RewardResult.builder()
                .expGained(expReward)
                .currentExp(user.getCurrentExp())
                .currentLevel(user.getCurrentLevel())
                .isLevelUp(leveledUp)
                .newFlower(awarded)
                .build();
    }

    /**
     * 미접속 페널티: -10 EXP (명세). 레벨 강등 없음(EXP만 조정).
     */
    public void applyInactivityPenalty(User user) {
        user.addExp(-10);
        missionSproutLogRepository.save(MissionSproutLog.builder()
                .user(user)
                .action(SproutLogAction.INACTIVITY_PENALTY)
                .expDelta(-10)
                .build());
    }

    /**
     * Lv.5 도달 시 후보 풀에서 사용자가 보유하지 않은 꽃 1종을 랜덤 지급한다.
     * 후보를 모두 보유 중이면 지급 스킵(null).
     */
    private FlowerType tryAwardFlower(User user) {
        List<UserFlowerCollection> owned = userFlowerCollectionRepository.findByUser_IdOrderByAchievedAtDesc(user.getId());
        EnumSet<FlowerType> ownedSet = EnumSet.noneOf(FlowerType.class);
        for (UserFlowerCollection c : owned) {
            ownedSet.add(c.getFlowerType());
        }

        List<FlowerType> candidates = new ArrayList<>();
        for (FlowerType type : FLOWER_POOL) {
            if (!ownedSet.contains(type)) {
                candidates.add(type);
            }
        }
        if (candidates.isEmpty()) {
            return null;
        }

        FlowerType picked = candidates.get(ThreadLocalRandom.current().nextInt(candidates.size()));
        userFlowerCollectionRepository.save(UserFlowerCollection.builder()
                .user(user)
                .flowerType(picked)
                .achievedAt(LocalDateTime.now())
                .build());
        return picked;
    }
}
