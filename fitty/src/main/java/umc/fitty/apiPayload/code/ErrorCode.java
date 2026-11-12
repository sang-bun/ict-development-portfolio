package umc.fitty.apiPayload.code;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    //Internal Server Error
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에 문제가 생겼습니다."),

    // Client Error
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "적절하지 않은 HTTP 메소드입니다."),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "요청 값의 타입이 잘못되었습니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "적절하지 않은 값입니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "해당 리소스를 찾을 수 없습니다."),
    MISSING_REQUEST_PARAMETER(HttpStatus.BAD_REQUEST, "필수 파라미터가 누락되었습니다."),

    GOAL_NOT_FOUND(HttpStatus.NOT_FOUND, "목표 정보를 찾을 수 없습니다."),

    CHARACTER_ALREADY_EXISTS(HttpStatus.CONFLICT, "캐릭터가 이미 존재 합니다."),

    CHARACTER_NOT_FOUND(HttpStatus.NOT_FOUND, "캐릭터 정보를 찾을 수 없습니다."),

    RECORD_NOT_FOUND(HttpStatus.NOT_FOUND, "기록 정보를 찾을 수 없습니다."),

    // User Error
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자의 정보를 찾을 수 없습니다."),
    EMAIL_ALREADY_USED(HttpStatus.BAD_REQUEST,"이미 존재하는 이메일입니다."),
    PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST,"비밀번호가 일치하지 않습니다"),
    NICKNAME_NOT_BLANK(HttpStatus.BAD_REQUEST,"닉네임은 필수 항목입니다."),
    AGE_INVALID(HttpStatus.BAD_REQUEST, "나이는 1~120 사이여야 합니다."),
    USER_DELETE_CONFLICT(HttpStatus.CONFLICT,"연결된 데이터가 있어 회원을 삭제할 수 없습니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}