package com.example.givy.domain.notification.exception;

import com.example.givy.global.apiPayLoad.code.BaseErrorCode;
import com.example.givy.global.apiPayLoad.exception.GeneralException;

public class NotificationException extends GeneralException {
    public NotificationException(BaseErrorCode code){
        super(code);
    }
}
