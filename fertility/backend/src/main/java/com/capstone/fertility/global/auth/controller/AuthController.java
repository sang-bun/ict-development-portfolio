package com.capstone.fertility.global.auth.controller;

import com.capstone.fertility.domain.user.dto.req.UserReqDTO;
import com.capstone.fertility.domain.user.dto.res.UserResDTO;
import com.capstone.fertility.domain.user.exception.code.UserSuccessCode;
import com.capstone.fertility.domain.user.service.command.UserCommandService;
import com.capstone.fertility.global.apiPayLoad.ApiResponse;
import com.capstone.fertility.global.apiPayLoad.code.GeneralSuccessCode;
import com.capstone.fertility.global.auth.service.AuthService;
import com.capstone.fertility.global.dto.TokenRefreshRequest;
import com.capstone.fertility.global.dto.TokenRefreshResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserCommandService userCommandService;
    private final AuthService authService;

    @PostMapping("/signup")
    public ApiResponse<UserResDTO.LoginResDTO> signUp(@RequestBody @Valid UserReqDTO.SignUpReqDTO request){

        UserResDTO.LoginResDTO response = userCommandService.signUp(request);

        return ApiResponse.onSuccess(UserSuccessCode.USER_SIGNUP_SUCCESS, response);
    }

    @PostMapping("/refresh")
    public ApiResponse<TokenRefreshResponse> refreshToken(@RequestBody TokenRefreshRequest request) {

        TokenRefreshResponse response = authService.refreshToken(request.getRefreshToken());

        return ApiResponse.onSuccess(GeneralSuccessCode.TOKEN_REFRESH_SUCCESS, response);
    }

    @PostMapping("/login")
    public ApiResponse<UserResDTO.LoginResDTO> login(@RequestBody @Valid UserReqDTO.LoginReqDTO request){
        UserResDTO.LoginResDTO response = userCommandService.login(request);

        return ApiResponse.onSuccess(UserSuccessCode.USER_LOGIN_SUCCESS, response);
    }
}