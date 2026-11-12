package com.capstone.fertility.global.llm.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "llm")
public class LlmProperties {

    private String apiKey = "";
    private String model = "claude-sonnet-4-6";
    private String baseUrl = "https://api.anthropic.com";
    private int connectTimeoutMs = 5000;
    private int readTimeoutMs = 30000;
    private double temperature = 0.7;
    private int maxTokens = 2048;
}
