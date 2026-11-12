package com.example.givy.global.apiPayLoad.handler;

import com.example.givy.global.apiPayLoad.ApiResponse;
import com.example.givy.global.apiPayLoad.code.GeneralErrorCode;
import com.example.givy.global.apiPayLoad.exception.GeneralException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GeneralExceptionAdvice {
    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<ApiResponse<Void>> handleException(
            GeneralException ex
    ){
        return ResponseEntity.status(ex.getCode().getStatus())
                .body(ApiResponse.onFailure(ex.getCode()));
    }
    //GeneralException에서 최상단에서 BaseErrorCode를 계속 예외가 나면서 초기화되는 거임.
    //도메인 별 UserException이나 ReivewException이 extends GeneralException해서 자바는 다형성이 되니까 업캐스팅해서 들어와짐.


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidation(MethodArgumentNotValidException e) {

        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.onFailure(GeneralErrorCode.REQUEST_BODY_VALIDATION_FAILED, message));
    }

}
