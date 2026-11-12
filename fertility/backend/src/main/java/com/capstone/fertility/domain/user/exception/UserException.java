package com.capstone.fertility.domain.user.exception;

import com.capstone.fertility.global.apiPayLoad.code.BaseErrorCode;
import com.capstone.fertility.global.apiPayLoad.exception.GeneralException;

public class UserException extends GeneralException {
    public UserException(BaseErrorCode code) {
        super(code);
    }
}