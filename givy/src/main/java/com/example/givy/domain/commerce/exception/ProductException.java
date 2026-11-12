package com.example.givy.domain.commerce.exception;

import com.example.givy.global.apiPayLoad.code.BaseErrorCode;
import com.example.givy.global.apiPayLoad.exception.GeneralException;

public class ProductException extends GeneralException {
    public ProductException(BaseErrorCode code){
        super(code);
    }
}
