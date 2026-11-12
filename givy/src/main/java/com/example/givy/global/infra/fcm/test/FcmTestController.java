package com.example.givy.global.infra.fcm.test;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Hidden //swagger 제외
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/test")
public class FcmTestController {

    private final FcmTestService fcmTestService;

    @GetMapping("/push")
    public String testPush(
            @RequestParam String token,
            @RequestParam String title,
            @RequestParam String body) {

        fcmTestService.sendTestPush(token, title, body);
        return "푸시 발송 요청 완료! (비동기로 전송되므로 로그를 확인하세요)";
    }
}