package com.example.givy.global.infra.fcm.test;

import com.example.givy.global.infra.fcm.dto.SingleNotificationRequest;
import com.example.givy.global.infra.fcm.service.FcmNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FcmTestService {

    private final FcmNotificationService fcmNotificationService;

    public void sendTestPush(String token, String title, String body) {
        // 1. 요청 객체 생성 (정적 팩토리 메서드 of 사용)
        SingleNotificationRequest request = SingleNotificationRequest.of(token, title, body);
        System.out.println("request = " + request);

        // 2. FCM 서비스 호출
        fcmNotificationService.sendMessage(request);
    }
}