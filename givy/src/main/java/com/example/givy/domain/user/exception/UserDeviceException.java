package com.example.givy.domain.user.exception;

import com.example.givy.global.apiPayLoad.code.BaseErrorCode;
import com.example.givy.global.apiPayLoad.exception.GeneralException;

public class UserDeviceException extends GeneralException {
    public UserDeviceException(BaseErrorCode code){
        super(code);
    }
}
