package com.example.givy.domain.settings.exception;

import com.example.givy.global.apiPayLoad.code.BaseErrorCode;
import com.example.givy.global.apiPayLoad.exception.GeneralException;

public class SettingException extends GeneralException {
    public SettingException(BaseErrorCode code) {
        super(code);
    }
}
