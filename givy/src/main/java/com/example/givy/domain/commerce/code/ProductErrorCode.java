package com.example.givy.domain.commerce.code;

import com.example.givy.global.apiPayLoad.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ProductErrorCode implements BaseErrorCode {
    PRODUCT_NOT_FOUND_BY_PRODUCT_ID(HttpStatus.NOT_FOUND, "PRODUCT404_1", "해당 상품을 찾을 수 없습니다."),
    PRODUCT_NOT_FOUND_BY_NAME(HttpStatus.NOT_FOUND, "PRODUCT404_2", "해당 상품을 찾을 수 없습니다.")
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
