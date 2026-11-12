package com.example.givy.domain.kis;

import com.example.givy.domain.kis.service.KisAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KisTokenInitializer implements CommandLineRunner {
    private final KisAuthService kisAuthService;

    @Override
    public void run(String... args) {
        // 서버 시작과 동시에 토큰 발급 테스트
        String token = kisAuthService.getStoredToken();
        System.out.println("초기 토큰 발급 검증 완료: " + (token != null));
    }
}