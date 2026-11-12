package com.example.givy.domain.home.controller;

import com.example.givy.domain.home.converter.HomeConverter;
import com.example.givy.domain.home.dto.res.HomeResDTO;
import com.example.givy.domain.home.exception.code.HomeSuccessCode;
import com.example.givy.domain.home.service.query.HomeQueryService;
import com.example.givy.global.apiPayLoad.ApiResponse;
import com.example.givy.global.security.CustomPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/home")
public class HomeController {

    private final HomeQueryService homeQueryService;

    @GetMapping
    public ApiResponse<HomeResDTO.HomeResponseDTO> getHome(@AuthenticationPrincipal CustomPrincipal principal){

        Long userId = principal.getUserId();

        HomeResDTO.HomeResponseDTO result = homeQueryService.getHome(userId);

        return ApiResponse.onSuccess(HomeSuccessCode.HOME_QUERY_SUCCESS, result);
    }
}
