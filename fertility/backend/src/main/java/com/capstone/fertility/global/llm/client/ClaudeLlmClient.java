package com.capstone.fertility.global.llm.client;

import com.capstone.fertility.global.llm.config.LlmProperties;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.netty.http.client.HttpClient;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Anthropic Claude Messages API ({@code POST /v1/messages}) 호출 구현체.
 */
@Slf4j
@Component
@EnableConfigurationProperties(LlmProperties.class)
public class ClaudeLlmClient implements LlmClient {

    private static final String ANTHROPIC_VERSION = "2023-06-01";
    private static final String JSON_SUFFIX = """

            [출력 형식 — 절대 엄수]
            응답은 반드시 단일 JSON 객체 문자열만 포함하세요. 마크다운 코드 펜스(```), 설명 문장, 주석을 붙이지 마세요.
            """;

    private final WebClient webClient;
    private final LlmProperties props;

    public ClaudeLlmClient(LlmProperties props) {
        this.props = props;

        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, props.getConnectTimeoutMs())
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(props.getReadTimeoutMs(), TimeUnit.MILLISECONDS)));

        this.webClient = WebClient.builder()
                .baseUrl(props.getBaseUrl())
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("anthropic-version", ANTHROPIC_VERSION)
                .build();
    }

    @Override
    public String chat(String systemPrompt, String userPrompt) {
        return call(systemPrompt, userPrompt, false);
    }

    @Override
    public String chatJson(String systemPrompt, String userPrompt) {
        return call(systemPrompt, userPrompt, true);
    }

    private String call(String systemPrompt, String userPrompt, boolean jsonMode) {
        String effectiveSystem = jsonMode
                ? (systemPrompt == null ? "" : systemPrompt) + JSON_SUFFIX
                : systemPrompt;

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", props.getModel());
        body.put("max_tokens", props.getMaxTokens());
        body.put("temperature", props.getTemperature());
        if (effectiveSystem != null && !effectiveSystem.isBlank()) {
            body.put("system", effectiveSystem);
        }
        body.put("messages", List.of(
                Map.of("role", "user", "content", userPrompt)
        ));

        try {
            Map<?, ?> response = webClient.post()
                    .uri("/v1/messages")
                    .header("x-api-key", props.getApiKey())
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response == null) {
                throw new RuntimeException("LLM 응답이 null입니다.");
            }

            String text = extractText(response);
            return jsonMode ? stripCodeFence(text) : text;

        } catch (WebClientResponseException e) {
            log.error("Claude API 호출 실패: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Claude API 호출 실패: " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private String extractText(Map<?, ?> response) {
        List<Map<String, Object>> content = (List<Map<String, Object>>) response.get("content");
        if (content == null || content.isEmpty()) {
            throw new RuntimeException("Claude content가 비어 있습니다. response=" + response);
        }

        for (Map<String, Object> block : content) {
            if ("text".equals(block.get("type"))) {
                Object text = block.get("text");
                if (text instanceof String s && !s.isBlank()) {
                    return s;
                }
            }
        }
        throw new RuntimeException("Claude 응답에 text 블록이 없습니다.");
    }

    private String stripCodeFence(String raw) {
        if (raw == null) {
            return null;
        }
        String trimmed = raw.trim();
        if (!trimmed.startsWith("```")) {
            return trimmed;
        }
        int firstNewline = trimmed.indexOf('\n');
        if (firstNewline < 0) {
            return trimmed.replaceAll("^```\\w*", "").replaceAll("```$", "").trim();
        }
        String withoutHeader = trimmed.substring(firstNewline + 1);
        if (withoutHeader.endsWith("```")) {
            withoutHeader = withoutHeader.substring(0, withoutHeader.length() - 3);
        }
        return withoutHeader.trim();
    }
}
