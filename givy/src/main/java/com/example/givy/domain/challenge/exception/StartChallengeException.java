package com.example.givy.domain.challenge.exception;

import com.example.givy.global.apiPayLoad.code.BaseErrorCode;
import com.example.givy.global.apiPayLoad.exception.GeneralException;

public class StartChallengeException extends GeneralException {
    public StartChallengeException(BaseErrorCode code){
        super(code);
    }
}
