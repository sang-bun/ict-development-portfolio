package com.capstone.fertility.global.config;

import com.capstone.fertility.domain.community.config.CommunityUploadProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

@Configuration
@RequiredArgsConstructor
public class CommunityWebConfig implements WebMvcConfigurer {

    private final CommunityUploadProperties uploadProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path dir = Path.of(uploadProperties.getDirectory()).toAbsolutePath().normalize();
        registry.addResourceHandler("/uploads/community/**")
                .addResourceLocations(dir.toUri().toString());
    }
}
