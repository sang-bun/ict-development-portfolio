package com.capstone.fertility.domain.user.controller;

import com.capstone.fertility.domain.user.dto.req.UserReqDTO;
import com.capstone.fertility.domain.user.dto.res.UserResDTO;
import com.capstone.fertility.domain.user.exception.code.UserSuccessCode;
import com.capstone.fertility.domain.user.service.command.UserCommandService;
import com.capstone.fertility.domain.user.service.query.UserQueryService;
import com.capstone.fertility.global.apiPayLoad.ApiResponse;
import com.capstone.fertility.global.security.CustomPrincipal;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserCommandService userCommandService;

    private final UserQueryService userQueryService;

    @GetMapping("/me")
    @Operation(summary = "내 프로필 조회", description = "nickname(계정), displayName(표시 이름), gender(M|F), profileImageUrl, isTermsAgreed")
    public ApiResponse<UserResDTO.UserInfoDTO> getMyInfo(
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        UserResDTO.UserInfoDTO response = userQueryService.getMyInfo(principal.getUserId());
        return ApiResponse.onSuccess(UserSuccessCode.USER_FETCH_SUCCESS, response);
    }

    @PatchMapping("/me")
    @Operation(summary = "내 정보 수정 API", description = """
            PATCH — 보낸 필드만 반영.
            - nickname, profileImageUrl
            - displayName: 표시 이름 1~20자(trim). nickname과 분리
            - gender: M | F (MALE/FEMALE 허용). 프로필·다음 검사 기본값
            """)
    public ApiResponse<UserResDTO.UserInfoDTO> updateMyInfo(
            @AuthenticationPrincipal CustomPrincipal principal,
            @RequestBody UserReqDTO.UpdateProfileDTO request
    ) {
        // principal에서 가져온 userId를 사용하여 안전하게 '나의' 정보만 수정합니다.
        UserResDTO.UserInfoDTO updatedUser = userCommandService.updateMyInfo(principal.getUserId(), request);

        // 성공 응답을 반환합니다.
        return ApiResponse.onSuccess(UserSuccessCode.USER_PROFILE_UPDATE_SUCCESS, updatedUser);
    }

    @DeleteMapping("/me")
    public ApiResponse<String> withdraw(
        @AuthenticationPrincipal CustomPrincipal principal
    ) {
        userCommandService.withdraw(principal.getUserId()); // 수정
        
        return ApiResponse.onSuccess(UserSuccessCode.USER_WITH_DRAW_SUCCESS, "회원 탈퇴가 완료되었습니다.");
    }
}

