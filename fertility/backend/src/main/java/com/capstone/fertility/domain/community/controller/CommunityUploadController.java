package com.capstone.fertility.domain.community.controller;

import com.capstone.fertility.domain.community.dto.res.CommunityResDTO;
import com.capstone.fertility.domain.community.exception.code.CommunitySuccessCode;
import com.capstone.fertility.domain.community.service.command.CommunityImageUploadService;
import com.capstone.fertility.global.apiPayLoad.ApiResponse;
import com.capstone.fertility.global.security.CustomPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/community/uploads")
public class CommunityUploadController {

    private final CommunityImageUploadService imageUploadService;

    @PostMapping("/images")
    @Operation(summary = "커뮤니티 이미지 업로드")
    public ApiResponse<CommunityResDTO.ImageUpload> uploadImage(
            @AuthenticationPrincipal CustomPrincipal principal,
            @RequestPart("file") MultipartFile file
    ) {
        CommunityResDTO.ImageUpload result = imageUploadService.upload(file);
        return ApiResponse.onSuccess(CommunitySuccessCode.IMAGE_UPLOADED, result);
    }
}
