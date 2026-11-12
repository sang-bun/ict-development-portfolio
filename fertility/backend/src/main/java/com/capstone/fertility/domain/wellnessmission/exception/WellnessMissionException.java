package com.capstone.fertility.domain.wellnessmission.exception;

import com.capstone.fertility.global.apiPayLoad.code.BaseErrorCode;
import com.capstone.fertility.global.apiPayLoad.exception.GeneralException;

public class WellnessMissionException extends GeneralException {
    public WellnessMissionException(BaseErrorCode code) {
        super(code);
    }
}
