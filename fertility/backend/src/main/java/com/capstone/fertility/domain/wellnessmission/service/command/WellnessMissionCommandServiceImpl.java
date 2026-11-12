package com.capstone.fertility.domain.wellnessmission.service.command;

import com.capstone.fertility.domain.mission.service.reward.MissionRewardService;
import com.capstone.fertility.domain.mission.service.reward.RewardResult;
import com.capstone.fertility.domain.user.entity.User;
import com.capstone.fertility.domain.wellnessmission.dto.res.WellnessMissionResDTO;
import com.capstone.fertility.domain.wellnessmission.entity.WellnessMission;
import com.capstone.fertility.domain.wellnessmission.entity.WellnessMissionOfferState;
import com.capstone.fertility.domain.wellnessmission.exception.WellnessMissionException;
import com.capstone.fertility.domain.wellnessmission.exception.code.WellnessMissionErrorCode;
import com.capstone.fertility.domain.wellnessmission.repository.WellnessMissionCycleCompletionRepository;
import com.capstone.fertility.domain.wellnessmission.repository.WellnessMissionRepository;
import com.capstone.fertility.domain.wellnessmission.service.WellnessMissionProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class WellnessMissionCommandServiceImpl implements WellnessMissionCommandService {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    /** 명세: 웰니스(일일) 미션 1개 완료당 +5 EXP, 하루 최대 3회(15 EXP). */
    private static final int EXP_PER_MISSION = 5;

    private final WellnessMissionRepository wellnessMissionRepository;
    private final WellnessMissionCycleCompletionRepository wellnessMissionCycleCompletionRepository;
    private final MissionRewardService missionRewardService;
    private final WellnessMissionProgressService wellnessMissionProgressService;

    @Override
    public WellnessMissionResDTO.CompleteResult complete(Long userId, Long missionId) {
        LocalDate today = LocalDate.now(KST);

        WellnessMission mission = wellnessMissionRepository.findById(missionId)
                .orElseThrow(() -> new WellnessMissionException(WellnessMissionErrorCode.WELLNESS_MISSION_NOT_FOUND));

        User user = mission.getUser();
        if (!user.getId().equals(userId)) {
            throw new WellnessMissionException(WellnessMissionErrorCode.WELLNESS_MISSION_NOT_OWNER);
        }

        Long latestResultId = wellnessMissionRepository.findMaxTestResultIdByUserId(userId).orElse(null);
        if (latestResultId == null || !mission.getTestResult().getId().equals(latestResultId)) {
            throw new WellnessMissionException(WellnessMissionErrorCode.WELLNESS_MISSION_NOT_LATEST_RESULT);
        }

        List<WellnessMission> pool = wellnessMissionRepository.findByUser_IdAndTestResult_IdOrderByIdAsc(userId, latestResultId);
        WellnessMissionProgressService.SynchronizedOffer snap =
                wellnessMissionProgressService.synchronizeAndLoadOfferedMissions(user, latestResultId, pool);
        WellnessMissionOfferState state = snap.state();
        if (state == null) {
            throw new WellnessMissionException(WellnessMissionErrorCode.WELLNESS_MISSION_NOT_FOUND);
        }

        if (!state.containsMissionId(missionId)) {
            throw new WellnessMissionException(WellnessMissionErrorCode.WELLNESS_MISSION_NOT_OFFERED);
        }

        int cycleIndex = state.getCycleIndex();
        if (wellnessMissionCycleCompletionRepository.existsByUser_IdAndWellnessMission_IdAndCycleIndex(userId, missionId, cycleIndex)) {
            return WellnessMissionResDTO.CompleteResult.builder()
                    .missionId(mission.getId())
                    .expGained(0)
                    .currentExp(user.getCurrentExp())
                    .currentLevel(user.getCurrentLevel())
                    .requiredExpForCurrentLevel(user.getRequiredExpForCurrentLevel())
                    .isLevelUp(false)
                    .alreadyCompleted(true)
                    .dailyRewardCapReached(false)
                    .newFlower(null)
                    .build();
        }

        user.alignDailyWellnessRewardCounter(today);
        boolean giveExp = user.hasRemainingDailyWellnessExpRewards();

        wellnessMissionProgressService.recordCycleCompletion(user, mission, latestResultId, cycleIndex);

        int exp = giveExp ? EXP_PER_MISSION : 0;
        RewardResult reward = missionRewardService.grantMissionCompletion(user, exp);
        if (giveExp) {
            user.incrementDailyWellnessExpRewards();
        }

        wellnessMissionProgressService.afterRecordedCompletion(userId, latestResultId, pool);

        return WellnessMissionResDTO.CompleteResult.builder()
                .missionId(mission.getId())
                .expGained(reward.expGained())
                .currentExp(reward.currentExp())
                .currentLevel(reward.currentLevel())
                .requiredExpForCurrentLevel(user.getRequiredExpForCurrentLevel())
                .isLevelUp(reward.isLevelUp())
                .alreadyCompleted(false)
                .dailyRewardCapReached(!giveExp)
                .newFlower(reward.newFlower() != null ? reward.newFlower().name() : null)
                .build();
    }
}
