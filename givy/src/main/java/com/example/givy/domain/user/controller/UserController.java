package com.example.givy.domain.user.controller;

import com.example.givy.domain.user.code.UserSuccessCode;
import com.example.givy.domain.user.dto.req.UserReqDTO;
import com.example.givy.domain.user.dto.res.UserResDTO;
import com.example.givy.domain.user.service.command.UserCommandService;
import com.example.givy.domain.user.service.query.UserQueryService;
import com.example.givy.global.apiPayLoad.ApiResponse;
import com.example.givy.global.security.CustomPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class UserController implements UserControllerDocs {

    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;

    /* 01-01 회원가입 API */
    @PostMapping("/signup")
    public ApiResponse<UserResDTO.UserInfoDTO> SignUp(
            @RequestBody @Valid UserReqDTO.UserSignupDTO request
    ) {
        return ApiResponse.onSuccess(UserSuccessCode.USER_SIGNUP_CREATED, userCommandService.signup(request));
    }

    /* 01-02 로그인 API */
    @PostMapping("/login")
    public ApiResponse<UserResDTO.UserLoginResDTO> Login(
            @RequestBody @Valid UserReqDTO.UserLoginDTO request
    ) {
        return ApiResponse.onSuccess(UserSuccessCode.USER_LOGIN_SUCCESS, userCommandService.login(request));
    }


    /* 01-04 소셜 회원가입(프로필 완성 단계) */
    @PostMapping("/social/signup")
    public ApiResponse<?> SocialSignup(
            @AuthenticationPrincipal CustomPrincipal principal,
            @RequestBody @Valid UserReqDTO.UserProfileDTO request
    ) {
        userCommandService.socialSignup(principal.getUserId(), request);
        return ApiResponse.onSuccess(UserSuccessCode.USER_SOCIAL_SIGNUP_CREATED);
    }


    /* 01-05 내 정보 조회 API */
    @GetMapping("/users/me")
    public ApiResponse<UserResDTO.UserMyPageDTO> getUserInfo(
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        UserResDTO.UserMyPageDTO response = userQueryService.getUserInfo(principal.getUserId());
        return ApiResponse.onSuccess(UserSuccessCode.USER_FETCH_SUCCESS, response);
    }

    /* 01-06 로그아웃 API */
    @PostMapping("/logout")
    public ApiResponse<?> logout(
            @AuthenticationPrincipal CustomPrincipal principal,
            @RequestBody @Valid UserReqDTO.UserLogoutDTO request
    ) {
        userCommandService.logout(principal.getUserId(), request.getRefreshToken());
        return ApiResponse.onSuccess(UserSuccessCode.USER_LOGOUT_SUCCESS);
    }

    /* 01-07 토큰 재발급 API */
    @PostMapping("/refresh")
    public ApiResponse<UserResDTO.UserTokenRefreshResDTO> refresh(
            @RequestBody @Valid UserReqDTO.UserTokenRefreshDTO request
    ) {
        return ApiResponse.onSuccess(
                UserSuccessCode.USER_TOKEN_REFRESH_SUCCESS,
                userCommandService.refresh(request.getRefreshToken())
        );
    }


}
