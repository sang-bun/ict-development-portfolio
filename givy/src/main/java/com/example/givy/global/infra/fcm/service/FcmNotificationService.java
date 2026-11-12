package com.example.givy.global.infra.fcm.service;

import com.example.givy.global.infra.fcm.code.FcmErrorCode;
import com.example.givy.global.infra.fcm.dto.FcmMessageDto;
import com.example.givy.global.infra.fcm.dto.MultiNotificationRequest;
import com.example.givy.global.infra.fcm.dto.SingleNotificationRequest;
import com.example.givy.global.infra.fcm.exception.FcmException;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

/**
 * Firebase 서버에 메시지 전송을 요청하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FcmNotificationService {
    //FcmConfig에서 생성한 FirebaseMessaging 빈을 주입
    private final FirebaseMessaging firebaseMessaging;

    //단일 발송
    public void sendMessage(final SingleNotificationRequest request) {
        try {
            val message = request.buildMessage().setApnsConfig(getApnsConfig(request)).build();

            //전송 요청 후 결과(Future)를 받음
            val future = firebaseMessaging.sendAsync(message);

            //결과가 돌아오면 실행될 로직
            ApiFutures.addCallback(future, new ApiFutureCallback<String>() {
                @Override
                public void onFailure(Throwable t) {
                    System.err.println("알림 전송 실패: " + t.getMessage());
                }

                @Override
                public void onSuccess(String result) {
                    System.out.println("알림 전송 성공! 메시지 ID: " + result);
                }
            }, MoreExecutors.directExecutor());

        } catch (RuntimeException exception) {
            throw new FcmException(FcmErrorCode.FCM_SERVICE_UNAVAILABLE);
        }
    }

    // 다중 발송을 단일 발송 루프로 대체
    public void sendMessage(final MultiNotificationRequest request) {
        request.getTargetTokens().forEach(token -> {
            try {
                Message message = Message.builder()
                        .setToken(token)
                        .setNotification(Notification.builder()
                                .setTitle(request.getTitle())
                                .setBody(request.getBody())
                                .build())
                        .setApnsConfig(getApnsConfig(request))
                        .build();

                firebaseMessaging.sendAsync(message); // 개별 비동기 전송
            } catch (Exception e) {
                log.error("토큰 전송 실패 : {}", token);
            }
        });
    }

    //ios 전용 설정 -> apple 기기는 별도의 APNs 설정을 해야 앱이 꺼져있을 때도 정상적으로 작동
    private ApnsConfig getApnsConfig(FcmMessageDto dto) {
        // 알림창에 뜰 제목 및 본문 설정
        val alert = ApsAlert.builder()
                .setTitle(dto.title())
                .setBody(dto.body())
                .build();

        // 알림 소리(Sound) 등을 포함한 iOS 전용 속성(Aps) 설정
        val aps = Aps.builder()
                .setAlert(alert)
                .setSound("default") // 기본 알림음 사용
                .build();

        // 최종적인 APNs 설정 객체 생성
        return ApnsConfig.builder()
                .setAps(aps)
                .build();
    }
}