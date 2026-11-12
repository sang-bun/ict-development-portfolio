package com.example.givy.domain.challenge.controller;

import com.example.givy.domain.challenge.code.StartChallengeSuccessCode;
import com.example.givy.domain.challenge.dto.req.StartChallengeReqDTO;
import com.example.givy.domain.challenge.dto.res.StartChallengeResDTO;
import com.example.givy.domain.challenge.service.command.StartChallengeCommandService;
import com.example.givy.domain.challenge.service.query.StartChallengeQueryService;
import com.example.givy.domain.user.code.UserSuccessCode;
import com.example.givy.domain.user.dto.req.UserSecuritiesReqDTO;
import com.example.givy.domain.user.dto.res.UserResDTO;
import com.example.givy.domain.user.dto.res.UserSecuritiesResDTO;
import com.example.givy.domain.user.service.command.securities.UserSecuritiesAccountCommandService;
import com.example.givy.domain.user.service.query.UserQueryService;
import com.example.givy.global.apiPayLoad.ApiResponse;
import com.example.givy.global.security.CustomPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/start-challenge")
public class StartChallengeController implements StartChallengeControllerDocs {

    private final UserSecuritiesAccountCommandService userSecuritiesAccountService;
    private final UserQueryService userQueryService;
    private final StartChallengeCommandService startChallengeCommandService;
    private final StartChallengeQueryService startChallengeQueryService;

    /* 06-01 증권 계좌 등록 */
    @PostMapping("/setup/securities")
    public ApiResponse<UserSecuritiesResDTO.UserSecuritiesListDTO> registerSecurities(


            @AuthenticationPrincipal CustomPrincipal principal,
            @RequestBody @Valid UserSecuritiesReqDTO.RegisterSecuritiesDTO dto
    ) {
        UserSecuritiesResDTO.UserSecuritiesListDTO securities = userSecuritiesAccountService.registerSecurities(principal.getUserId(), dto);

        //요청된 계좌 모두가 이미 등록된 경우
        if(securities.getSecuritiesList().isEmpty()){
            return ApiResponse.onSuccess(UserSuccessCode.USER_SECURITIES_ALREADY_CREATED, null);
        }

        //요청된 계좌 일부가 등록된 경우
        if(securities.getSecuritiesList().size() < dto.getSecuritiesList().size()){
            return ApiResponse.onSuccess(UserSuccessCode.SOME_USER_SECURITIES_CREATED, securities);
        }
        
        //계좌 모두가 등록되지 않은 경우
        return ApiResponse.onSuccess(UserSuccessCode.USER_SECURITIES_CREATED, securities);
    }

    /* 06-02 성인 여부 판단 */
    @GetMapping("/setup/age-check")
    public ApiResponse<UserResDTO.AgeVerificationResDTO> verifyAdult(
            @AuthenticationPrincipal CustomPrincipal principal) {
        return ApiResponse.onSuccess(UserSuccessCode.USER_ADULT_CHECK_SUCCESS, userQueryService.verifyAdult(principal.getUserId()));
    }

    /* 06-03 스타트 챌린지 시작 */
    @PostMapping()
    public ApiResponse<StartChallengeResDTO.StartChallengeInfoDTO> joinChallenge(
            @AuthenticationPrincipal CustomPrincipal principal,
            @RequestBody @Valid StartChallengeReqDTO.joinChallengeDTO dto){
        return ApiResponse.onSuccess(StartChallengeSuccessCode.START_CHALLENGE_CREATED, startChallengeCommandService.joinChallenge(principal.getUserId(), dto));
    }

    /* 06-04 스타트 챌린지 완료 */
    @PatchMapping("/{startChallengeId}")
    public ApiResponse<StartChallengeResDTO.StartChallengeInfoDTO> completeChallenge(
            @AuthenticationPrincipal CustomPrincipal principal,
            @PathVariable(name = "startChallengeId") Long startChallengeId
    ){
        StartChallengeResDTO.StartChallengeInfoDTO result = startChallengeCommandService.completeChallenge(principal.getUserId(), startChallengeId);

        if(result == null) {
            return ApiResponse.onSuccess(StartChallengeSuccessCode.START_CHALLENGE_COMPLETED, null);
        }

        return ApiResponse.onSuccess(StartChallengeSuccessCode.START_CHALLENGE_COMPLETED, result);
    }

    /* 06-05 스타트 챌린지 상태 조회 */
    @GetMapping()
    public ApiResponse<List<StartChallengeResDTO.StartChallengeStatusDTO>> getChallengeStatus(
            @AuthenticationPrincipal CustomPrincipal principal){
        List<StartChallengeResDTO.StartChallengeStatusDTO> result = startChallengeQueryService.getChallengeStatus(principal.getUserId());

        if(result == null){
            return ApiResponse.onSuccess(StartChallengeSuccessCode.START_CHALLENGE_NOT_FOUND, null);
        }

        return ApiResponse.onSuccess(StartChallengeSuccessCode.START_CHALLENGE_STATUS_FOUND, result);
    }
}
