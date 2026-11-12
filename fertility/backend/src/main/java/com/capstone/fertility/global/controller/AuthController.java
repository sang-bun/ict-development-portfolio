// package com.capstone.fertility.global.controller;

// import com.capstone.fertility.global.apiPayLoad.ApiResponse;
// import com.capstone.fertility.global.apiPayLoad.code.GeneralSuccessCode;
// import com.capstone.fertility.global.dto.*;
// import com.capstone.fertility.global.auth.service.AuthService;
// import lombok.RequiredArgsConstructor;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// @RestController
// @RequestMapping("/auth")
// @RequiredArgsConstructor
// public class AuthController {
    
//     private final AuthService authService;
    
//     @PostMapping("/refresh")
//     public ApiResponse<TokenRefreshResponse> refreshToken(@RequestBody TokenRefreshRequest request) {
//         TokenRefreshResponse response = authService.refreshToken(request.getRefreshToken());
//         return ApiResponse.onSuccess(GeneralSuccessCode.TOKEN_REFRESH_SUCCESS, response);
//     }
// }