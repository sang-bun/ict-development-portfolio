package com.example.givy.domain.notification.scheduler;

import com.example.givy.domain.commerce.enums.MarketCode;
import com.example.givy.domain.notification.service.command.NotificationCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationScheduler {
    private final NotificationCommandService notificationCommandService;

    /**
     * 한국 시장 개장 알림 (매주 평일 오전 9시)
     * 초 분 시 일 월 요일
     */
    @Scheduled(cron = "0 0 9 * * MON-FRI", zone = "Asia/Seoul")
//    @Scheduled(cron = "0 * * * * *", zone = "Asia/Seoul")   //Scheduler test
    public void scheduleKoreaMarketOpen() {
        log.info("한국 시장 개장 알림 스케줄러 실행");
        notificationCommandService.sendMarketOpenPush(MarketCode.KR);
    }

    /**
     * 미국 시장 개장 알림 (매주 평일 밤 10시 30분 - 서머타임 미고려 기준)
     * 서머타임 적용 시 밤 9시 30분으로 조정 필요
     */
    @Scheduled(cron = "0 30 22 * * MON-FRI", zone = "Asia/Seoul")
    public void scheduleUSMarketOpen() {
        log.info("미국 시장 개장 알림 스케줄러 실행");
        notificationCommandService.sendMarketOpenPush(MarketCode.US);
    }
}