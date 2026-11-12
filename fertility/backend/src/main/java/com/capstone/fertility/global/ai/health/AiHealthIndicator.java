package com.capstone.fertility.global.ai.health;

import com.capstone.fertility.global.ai.config.AiProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component("aiServer")
@RequiredArgsConstructor
public class AiHealthIndicator implements HealthIndicator {

    @Qualifier("aiWebClient")
    private final WebClient aiWebClient;
    private final AiProperties aiProperties;

    @Override
    public Health health() {
        String url = joinBaseUrlAndPath(aiProperties.getBaseUrl(), aiProperties.getHealthPath());
        try {
            Integer statusCode = aiWebClient.get()
                    .uri(url)
                    .retrieve()
                    .toBodilessEntity()
                    .map(entity -> entity.getStatusCode().value())
                    .block();

            if (statusCode != null && statusCode >= 200 && statusCode < 400) {
                return Health.up()
                        .withDetail("url", url)
                        .withDetail("status", statusCode)
                        .build();
            }
            return Health.down()
                    .withDetail("url", url)
                    .withDetail("status", statusCode)
                    .build();
        } catch (Exception e) {
            return Health.down(e)
                    .withDetail("url", url)
                    .build();
        }
    }

    private static String joinBaseUrlAndPath(String base, String path) {
        if (base == null) {
            base = "";
        }
        if (path == null) {
            path = "";
        }
        String b = base.trim();
        String p = path.trim();
        if (b.endsWith("/")) {
            b = b.substring(0, b.length() - 1);
        }
        if (!p.startsWith("/")) {
            p = "/" + p;
        }
        return b + p;
    }
}
