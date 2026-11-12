package com.capstone.fertility.global.ai.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "ai")
public class AiProperties {

    /**
     * 예: http://localhost:8000
     */
    private String baseUrl = "http://localhost:8000";

    /**
     * AI 서버 연결 타임아웃 (ms)
     */
    private int connectTimeoutMs = 2000;

    /**
     * AI 서버 응답 대기 타임아웃 (ms)
     */
    private int readTimeoutMs = 5000;

    /**
     * HealthIndicator가 호출할 경량 health endpoint
     */
    private String healthPath = "/health";

    private Prediction prediction = new Prediction();

    @Getter
    @Setter
    public static class Prediction {
        private boolean enabled = true;
        private Path path = new Path();
    }

    @Getter
    @Setter
    public static class Path {
        private String male = "/api/predict/male";
        private String female = "/api/predict/female";
    }
}
