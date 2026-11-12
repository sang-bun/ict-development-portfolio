package com.capstone.fertility.global.apiPayLoad;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "공통 API 응답 형식")
abstract class ApiResponseSchema<T> {

    @Schema(description = "요청 성공 여부", example = "true")
    private boolean isSuccess;

    @Schema(description = "응답 코드", example = "COMMON200") // 범용적인 코드로 수정
    private String code;

    @Schema(description = "응답 메시지", example = "요청에 성공하였습니다.") // 범용적인 메시지로 수정
    private String message;

    @Schema(description = "실제 데이터")
    private T result;
}