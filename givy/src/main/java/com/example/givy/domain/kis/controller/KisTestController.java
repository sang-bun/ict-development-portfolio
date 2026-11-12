package com.example.givy.domain.kis.controller;

import com.example.givy.domain.kis.scheduler.KisStockScheduler;
import com.example.givy.global.apiPayLoad.ApiResponse;
import com.example.givy.global.apiPayLoad.code.GeneralSuccessCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test/kis")
@RequiredArgsConstructor
@Slf4j
public class KisTestController {

    private final KisStockScheduler kisStockScheduler;

    /**
     * 테스트용: 스케줄러 수동 실행
     * 배포 후 16:00까지 기다리지 않고 바로 테스트할 수 있도록 하는 엔드포인트
     */
    @PostMapping("/trigger-scheduler")
    public ApiResponse<String> triggerScheduler() {
        log.info("테스트용 스케줄러 수동 실행 요청");
        try {
            kisStockScheduler.updateStockInfo();
            return ApiResponse.onSuccess(GeneralSuccessCode.OK, "스케줄러 실행 완료");
        } catch (Exception e) {
            log.error("스케줄러 실행 중 오류 발생", e);
            return ApiResponse.onFailure(com.example.givy.global.apiPayLoad.code.GeneralErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
