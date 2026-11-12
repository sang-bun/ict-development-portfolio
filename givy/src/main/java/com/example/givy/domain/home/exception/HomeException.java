package com.example.givy.domain.home.exception;

import com.example.givy.global.apiPayLoad.code.BaseErrorCode;
import com.example.givy.global.apiPayLoad.exception.GeneralException;

public class HomeException extends GeneralException {
    public HomeException(BaseErrorCode code) {
        super(code);
    }
}
