package com.example.givy.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import jakarta.annotation.PostConstruct;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

/**
 * FCM(Firebase Cloud Messaging) 설정을 담당하는 클래스
 * 스프링 서버가 뜰 때 Firebase SDK 초기화 하고 알림을 보낼 때 사용할 'FirebaseMessaging' 객체를 스프링 빈으로 등록
 */

@Configuration
public class FcmConfig {
    //Firebase 서비스 키 파일(.json)을 읽어오기 위한 리소스 객체
    private final ClassPathResource firebaseResource;

    //Firebase 콘솔에서 생성한 프로젝트 고유 ID
    private final String projectId;


    //생성자 주입을 통해 application.yml에 정의된 설정 값 가져오기
    public FcmConfig(
            @Value("${fcm.file_path}") String firebaseFilePath,
            @Value("${fcm.project_id}") String projectId
    ){
        //resources 폴더 내의 서비스 키 파일을 가리키는 객체 생성
        this.firebaseResource = new ClassPathResource(firebaseFilePath);
        this.projectId = projectId;
    }

    //Firebase SDK를 실제로 로그인 시키는 초기화 과정
    @PostConstruct
    public void init() throws IOException {
        val option = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials
                        .fromStream(firebaseResource.getInputStream()))
                .setProjectId(projectId)
                .build();

        if(FirebaseApp.getApps().isEmpty()){
            FirebaseApp.initializeApp(option);
        }
    }

    //실제 알림 메시지 발송 객체
    //서비스 로직에서 @Autowired나 생성자 주입으로 객체 가져온 후 알림 발송
    @Bean
    FirebaseMessaging firebaseMessaging(){
        return FirebaseMessaging.getInstance(firebaseApp());
    }

    //초기화 된 FirebaseApp 인스턴스 관리
    @Bean
    FirebaseApp firebaseApp(){
        return FirebaseApp.getInstance();
    }
}