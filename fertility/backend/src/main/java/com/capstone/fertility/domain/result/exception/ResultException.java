package com.capstone.fertility.domain.result.exception;

import com.capstone.fertility.global.apiPayLoad.code.BaseErrorCode;
import com.capstone.fertility.global.apiPayLoad.exception.GeneralException;

public class ResultException extends GeneralException {
    public ResultException(BaseErrorCode code) {
        super(code);
    }
}
