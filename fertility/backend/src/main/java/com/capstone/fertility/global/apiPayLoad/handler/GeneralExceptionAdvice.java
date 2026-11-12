package com.capstone.fertility.global.apiPayLoad.handler;

import com.capstone.fertility.global.apiPayLoad.ApiResponse;
import com.capstone.fertility.global.apiPayLoad.code.GeneralErrorCode;
import com.capstone.fertility.global.apiPayLoad.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Arrays;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GeneralExceptionAdvice {

    private final Environment environment;

    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<ApiResponse<Void>> handleException(GeneralException ex) {
        return ResponseEntity.status(ex.getCode().getStatus())
                .body(ApiResponse.onFailure(ex.getCode()));
    }

    /**
     * Bean Validation 실패. 모든 검증 실패의 응답 형태를 통일한다.
     * - code: REQ400
     * - result: { field, reason } 객체 1건 (첫 번째 위반 사유)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<FieldViolation>> handleValidation(MethodArgumentNotValidException e) {
        var firstError = e.getBindingResult().getFieldError();
        FieldViolation detail = firstError != null
                ? new FieldViolation(firstError.getField(), firstError.getDefaultMessage())
                : new FieldViolation(null, "요청 본문이 유효하지 않습니다.");
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.onFailure(GeneralErrorCode.REQUEST_BODY_VALIDATION_FAILED, detail));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnreadableBody(HttpMessageNotReadableException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.onFailure(GeneralErrorCode.JSON_PARSE_ERROR));
    }

    /**
     * 잘못된 경로(존재하지 않는 URL) → 404.
     * 정적 리소스 경로가 매칭되지 않을 때 Spring 6.1+에서 던지는 예외.
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoResource(NoResourceFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.onFailure(GeneralErrorCode.NOT_FOUND));
    }

    /** 디스패처에 매핑된 핸들러가 없을 때 (드물게 발생) → 404 */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoHandler(NoHandlerFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.onFailure(GeneralErrorCode.NOT_FOUND));
    }

    /** PathVariable / RequestParam 타입 불일치 (예: /complete/undefined) → 400 */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<FieldViolation>> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        FieldViolation detail = new FieldViolation(
                e.getName(),
                "값이 올바른 타입이 아닙니다. (전달된 값: " + String.valueOf(e.getValue()) + ")"
        );
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.onFailure(GeneralErrorCode.TYPE_MISMATCH, detail));
    }

    /** 필수 RequestParam 누락 → 400 */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<FieldViolation>> handleMissingParam(MissingServletRequestParameterException e) {
        FieldViolation detail = new FieldViolation(e.getParameterName(), "필수 파라미터가 누락되었습니다.");
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.onFailure(GeneralErrorCode.REQUEST_PARAM_VALIDATION_FAILED, detail));
    }

    /** 잘못된 HTTP 메서드 → 405 (envelope은 유지) */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiResponse.onFailure(GeneralErrorCode.BAD_REQUEST));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiResponse<String>> handleDataAccess(DataAccessException e) {
        log.error("Database access failed", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.onFailure(GeneralErrorCode.INTERNAL_SERVER_ERROR, localDebugDetail(e)));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleUnexpected(Exception e) {
        log.error("Unhandled server error", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.onFailure(GeneralErrorCode.INTERNAL_SERVER_ERROR, localDebugDetail(e)));
    }

    private String localDebugDetail(Exception e) {
        if (!Arrays.asList(environment.getActiveProfiles()).contains("local")) {
            return null;
        }
        String message = e.getMessage();
        if (message == null || message.isBlank()) {
            return e.getClass().getSimpleName();
        }
        return e.getClass().getSimpleName() + ": " + message;
    }

    /**
     * Validation / 타입 불일치 등 필드 단위 위반을 표준 응답으로 내려준다.
     * 모든 4xx 오류 응답의 result는 null 또는 이 객체 형태로 통일된다.
     */
    public record FieldViolation(String field, String reason) {}
}
