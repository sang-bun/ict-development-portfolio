package com.capstone.fertility.domain.community.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.community.upload")
public class CommunityUploadProperties {

    /** 로컬 저장 디렉터리 (상대 경로는 프로젝트 루트 기준) */
    private String directory = "uploads/community";

    /** 클라이언트가 접근할 URL prefix (예: http://host:8080/uploads/community) */
    private String publicBaseUrl = "";
}
