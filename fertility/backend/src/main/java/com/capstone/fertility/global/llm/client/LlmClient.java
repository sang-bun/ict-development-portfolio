package com.capstone.fertility.global.llm.client;

/**
 * Anthropic Claude Messages API 호출 추상화.
 */
public interface LlmClient {

    /**
     * 자유 텍스트 응답을 받는다.
     */
    String chat(String systemPrompt, String userPrompt);

    /**
     * 응답을 JSON 객체 문자열로만 받는다.
     */
    String chatJson(String systemPrompt, String userPrompt);
}
