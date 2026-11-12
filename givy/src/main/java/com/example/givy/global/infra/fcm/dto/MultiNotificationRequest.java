package com.example.givy.global.infra.fcm.dto;

import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MultiNotificationRequest implements FcmMessageDto {

    private List<String> targetTokens;  //기기 토큰 List
    private String title;
    private String body;

    @Builder(access = AccessLevel.PRIVATE)
    private MultiNotificationRequest(@NonNull List<String> targetTokens, String title, String body) {
        this.targetTokens = targetTokens;
        this.title = title;
        this.body = body;
    }

    public static MultiNotificationRequest of(List<String> tokens, String title, String body) {
        return MultiNotificationRequest.builder()
                .targetTokens(tokens)
                .title(title)
                .body(body)
                .build();
    }
    
    @Override
    public String title() { return title; }

    @Override
    public String body() { return body; }

    /**
     * 여러 명에게 보낼 메시지 객체 생성
     */
    public MulticastMessage.Builder buildMessage() {
        return MulticastMessage.builder()
                .setNotification(toNotification())
                .addAllTokens(targetTokens);
    }

    @Override
    public Notification toNotification() {
        return Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();
    }
}