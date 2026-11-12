package com.example.givy.domain.user.controller;

import com.example.givy.domain.user.dto.req.UserReqDTO;
import com.example.givy.domain.user.dto.res.UserResDTO;
import com.example.givy.global.apiPayLoad.ApiResponse;
import com.example.givy.global.security.CustomPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "01 Auth / User", description = "01번대 인증 및 사용자 API")
@RequestMapping("/auth")
public interface UserControllerDocs {

    /* 01-01 회원가입 */
    @Operation(
            summary = "01-01 회원가입",
            operationId = "01-01",
            description = "이메일, 비밀번호, 이름, 닉네임, 언어 정보를 입력하여 회원가입을 진행합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "회원가입 성공 (USER_SIGNUP_CREATED)",
                    content = @Content(schema = @Schema(implementation = UserResDTO.UserInfoDTO.class))
            )
    })
    @PostMapping("/signup")
    ApiResponse<UserResDTO.UserInfoDTO> SignUp(
            @Valid @RequestBody UserReqDTO.UserSignupDTO request
    );

    /* 01-02 로그인 */
    @Operation(
            summary = "01-02 로그인",
            operationId = "01-02",
            description = "이메일과 비밀번호로 로그인하여 Access Token과 Refresh Token을 발급합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "로그인 성공 (USER_LOGIN_SUCCESS)",
                    content = @Content(schema = @Schema(implementation = UserResDTO.UserLoginResDTO.class))
            )
    })
    @PostMapping("/login")
    ApiResponse<UserResDTO.UserLoginResDTO> Login(
            @Valid @RequestBody UserReqDTO.UserLoginDTO request
    );

    /* 01-04 소셜 회원가입 (프로필 완성) */
    @Operation(
            summary = "01-04 소셜 회원가입 (프로필 완성)",
            operationId = "01-04",
            description = "소셜 로그인 후 추가 정보(이름, 닉네임, 언어 등)를 입력하여 회원가입을 완료합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "소셜 회원가입 완료 (USER_SOCIAL_SIGNUP_CREATED)"
            )
    })
    @PostMapping("/social/signup")
    ApiResponse<?> SocialSignup(
            @AuthenticationPrincipal CustomPrincipal principal,
            @Valid @RequestBody UserReqDTO.UserProfileDTO request
    );

    /* 01-05 내 정보 조회 */
    @Operation(
            summary = "01-05 내 정보 조회",
            operationId = "01-05",
            description = "JWT Access Token을 통해 로그인한 사용자의 정보를 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "내 정보 조회 성공 (USER_FETCH_SUCCESS)",
                    content = @Content(schema = @Schema(implementation = UserResDTO.UserDetailDTO.class))
            )
    })
    @GetMapping("/users/me")
    ApiResponse<UserResDTO.UserMyPageDTO> getUserInfo(
            @AuthenticationPrincipal CustomPrincipal principal
    );

    /* 01-06 로그아웃 */
    @Operation(
            summary = "01-06 로그아웃",
            operationId = "01-06",
            description = "현재 로그인된 사용자의 Refresh Token을 폐기하여 로그아웃 처리합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "로그아웃 성공 (USER_LOGOUT_SUCCESS)"
            )
    })
    @PostMapping("/logout")
    ApiResponse<?> logout(
            @AuthenticationPrincipal CustomPrincipal principal,
            @Valid @RequestBody UserReqDTO.UserLogoutDTO request
    );

    /* 01-07 토큰 재발급 */
    @Operation(
            summary = "01-07 토큰 재발급",
            operationId = "01-07",
            description = "유효한 Refresh Token을 사용하여 새로운 Access Token을 발급합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "토큰 재발급 성공 (USER_TOKEN_REFRESH_SUCCESS)",
                    content = @Content(schema = @Schema(implementation = UserResDTO.UserTokenRefreshResDTO.class))
            )
    })
    @PostMapping("/refresh")
    ApiResponse<UserResDTO.UserTokenRefreshResDTO> refresh(
            @Valid @RequestBody UserReqDTO.UserTokenRefreshDTO request
    );
}
