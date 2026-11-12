package com.example.givy.global.infra.fcm.exception;

import com.example.givy.global.apiPayLoad.code.BaseErrorCode;
import com.example.givy.global.apiPayLoad.exception.GeneralException;

public class FcmException extends GeneralException {
    public FcmException(BaseErrorCode code){
        super(code);
    }
}
