package com.example.givy.domain.challenge.controller;

import com.example.givy.domain.challenge.dto.req.StartChallengeReqDTO;
import com.example.givy.domain.challenge.dto.res.StartChallengeResDTO;
import com.example.givy.domain.user.dto.req.UserSecuritiesReqDTO;
import com.example.givy.domain.user.dto.res.UserResDTO;
import com.example.givy.domain.user.dto.res.UserSecuritiesResDTO;
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

import java.util.List;

@Tag(name = "06 Start Challenge", description = "06번대 스타트챌린지 API")
public interface StartChallengeControllerDocs {

    /* 06-01 증권 계좌 등록 */
    @Operation(
            summary = "06-01 증권 계좌 등록",
            operationId = "06-01",
            description = "스타트 챌린지 시작 전 사용자가 선택한 계좌 정보가 등록됩니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "계좌 등록 성공 (USER201_2)",
                    content = @Content(schema = @Schema(implementation = UserSecuritiesResDTO.UserSecuritiesListDTO.class))
            )
    })
    @PostMapping("/setup/securities")
    ApiResponse<UserSecuritiesResDTO.UserSecuritiesListDTO> registerSecurities(
            @AuthenticationPrincipal CustomPrincipal principal,
            @RequestBody @Valid UserSecuritiesReqDTO.RegisterSecuritiesDTO dto
    );

    /* 06-02 성인 여부 판단 */
    @Operation(
            summary = "06-02 성인 여부 판단",
            operationId = "06-02",
            description = "증권 계좌를 소지하고 있지 않은 사용자의 새 계좌 등록 시 성인 여부를 판단합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "성인 여부 판단 성공 (USER200_4)",
                    content = @Content(schema = @Schema(implementation = UserResDTO.AgeVerificationResDTO.class))
            )
    })
    @GetMapping("/setup/age-check")
    ApiResponse<UserResDTO.AgeVerificationResDTO> verifyAdult(@AuthenticationPrincipal CustomPrincipal principal);


    /* 06-03 스타트 챌린지 시작 */
    @Operation(
            summary = "06-03 스타트 챌린지 시작",
            operationId = "06-03",
            description = "스타트 챌린지를 시작합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "챌린지 참여 성공 (CHALLENGE201_1)",
                    content = @Content(schema = @Schema(implementation = StartChallengeResDTO.StartChallengeInfoDTO.class))
            )
    })
    @PostMapping()
    ApiResponse<StartChallengeResDTO.StartChallengeInfoDTO> joinChallenge(
            @AuthenticationPrincipal CustomPrincipal principal,
            @RequestBody @Valid StartChallengeReqDTO.joinChallengeDTO dto);


    /* 06-04 스타트 챌린지 완료 */
    @Operation(
            summary = "06-04 스타트 챌린지 완료",
            operationId = "06-04",
            description = "사용자의 상품 매수가 완료되면 챌린지 상태를 완료로 변경하고 챌린지 도장을 지급합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "챌린지 완료 (CHALLENGE200_1)",
                    content = @Content(schema = @Schema(implementation = StartChallengeResDTO.StartChallengeInfoDTO.class))
            )
    })
    @PatchMapping("/{startChallengeId}")
    public ApiResponse<StartChallengeResDTO.StartChallengeInfoDTO> completeChallenge(
            @AuthenticationPrincipal CustomPrincipal principal,
            @PathVariable Long startChallengeId
    );

    /* 06-05 스타트 챌린지 상태 조회 */
    @Operation(
            summary = "06-05 스타트 챌린지 상태 조회",
            operationId = "06-05",
            description = "사용자의 스타트 챌린지 상태를 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 완료 (CHALLENGE200_2)",
                    content = @Content(schema = @Schema(implementation = StartChallengeResDTO.StartChallengeStatusDTO.class))
            )
    })
    @GetMapping()
    ApiResponse<List<StartChallengeResDTO.StartChallengeStatusDTO>> getChallengeStatus(@AuthenticationPrincipal CustomPrincipal principal);

}