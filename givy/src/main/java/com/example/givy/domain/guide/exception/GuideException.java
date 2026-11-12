package com.example.givy.domain.guide.exception;

import com.example.givy.global.apiPayLoad.code.BaseErrorCode;
import com.example.givy.global.apiPayLoad.exception.GeneralException;

public class GuideException extends GeneralException {
    public GuideException(BaseErrorCode code) {
        super(code);
    }
}
