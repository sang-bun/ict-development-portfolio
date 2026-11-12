package com.capstone.fertility.domain.mission.scheduler;

import com.capstone.fertility.domain.mission.service.reward.MissionRewardService;
import com.capstone.fertility.domain.user.entity.User;
import com.capstone.fertility.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

/**
 * 명세: 2일 이상 미션 미수행 시 -10 EXP (레벨 강등 없음).
 * 매일 12:00(KST)에 last_mission_date가 2일 전 자정 이전인 사용자에게 최대 1회 적용.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SproutInactivityPenaltyScheduler {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final UserRepository userRepository;
    private final MissionRewardService missionRewardService;

    @Scheduled(cron = "0 0 12 * * *", zone = "Asia/Seoul")
    @Transactional
    public void applyDailyPenalty() {
        LocalDate today = LocalDate.now(KST);
        LocalDateTime cutoff = today.minusDays(2).atStartOfDay(KST).toLocalDateTime();
        List<User> candidates = userRepository.findByLastMissionDateBefore(cutoff);
        int applied = 0;
        for (User u : candidates) {
            if (u.getLastInactivityPenaltyDate() != null && u.getLastInactivityPenaltyDate().equals(today)) {
                continue;
            }
            missionRewardService.applyInactivityPenalty(u);
            u.setLastInactivityPenaltyDate(today);
            applied++;
        }
        if (applied > 0) {
            log.info("Sprout inactivity penalty applied to {} users (KST {})", applied, today);
        }
    }
}
