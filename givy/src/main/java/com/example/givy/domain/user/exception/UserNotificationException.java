package com.example.givy.domain.user.exception;

import com.example.givy.global.apiPayLoad.code.BaseErrorCode;
import com.example.givy.global.apiPayLoad.exception.GeneralException;

public class UserNotificationException extends GeneralException {
    public UserNotificationException(BaseErrorCode code){
        super(code);
    }
}
