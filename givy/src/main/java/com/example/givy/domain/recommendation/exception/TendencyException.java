package com.example.givy.domain.recommendation.exception;

import com.example.givy.global.apiPayLoad.code.BaseErrorCode;
import com.example.givy.global.apiPayLoad.exception.GeneralException;

public class TendencyException extends GeneralException {
    public TendencyException(BaseErrorCode code) {
        super(code);
    }
}
