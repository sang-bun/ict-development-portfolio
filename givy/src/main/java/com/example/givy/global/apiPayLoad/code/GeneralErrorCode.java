package com.example.givy.global.apiPayLoad.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor //final 필드는 단 한번만 초기화됨. - 생성자가 있어야 함.
public enum GeneralErrorCode implements BaseErrorCode{

    //enum 상수 선언 (생성자 호출) - 각 필드 순서대로 매핑됨.
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400_1", "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH401_1", "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "AUTH403_1", "요청이 거부되었습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON404_1", "요청한 리소스를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500_1", "예기치 않은 서버 오류입니다."),
    EXPIRED_OR_REVOKED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH401_2", "Refresh Token이 만료되었거나 더 이상 유효하지 않습니다."),

    TOKEN_HASH_FAILED(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "AUTH500_1",
            "인증 토큰 처리 중 서버 내부 오류가 발생했습니다."
    ),

    // [요청 본문 - DTO 검증 실패]
    REQUEST_BODY_VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "REQ400", "요청 본문(Request Body)의 유효성 검증에 실패했습니다."),

    // [경로 변수 - PathVariable 검증 실패]
    PATH_VARIABLE_VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "PATH400", "경로 변수(PathVariable)의 유효성 검증에 실패했습니다."),

    // [요청 파라미터 - RequestParam 검증 실패]
    REQUEST_PARAM_VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "PARAM400", "요청 파라미터(RequestParam)의 유효성 검증에 실패했습니다."),

    // [Form 데이터 바인딩 실패 (BindException)]
    FORM_BINDING_FAILED(HttpStatus.BAD_REQUEST, "BIND400", "요청 데이터 바인딩에 실패했습니다."),

    // [타입 불일치 (ex: Long에 문자열 전달)]
    TYPE_MISMATCH(HttpStatus.BAD_REQUEST, "TYPE400", "요청 파라미터 타입이 일치하지 않습니다."),

    // [JSON 파싱 실패]
    JSON_PARSE_ERROR(HttpStatus.BAD_REQUEST, "JSON400", "요청 본문(JSON)을 읽을 수 없습니다."),

    // [그 외]
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "COMMON400", "유효성 검증에 실패했습니다.");


    private final HttpStatus status;
    private final String code;
    private final String message;

}
