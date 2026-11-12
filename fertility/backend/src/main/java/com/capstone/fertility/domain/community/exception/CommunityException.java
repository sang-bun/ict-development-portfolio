package com.capstone.fertility.domain.community.exception;

import com.capstone.fertility.global.apiPayLoad.code.BaseErrorCode;
import com.capstone.fertility.global.apiPayLoad.exception.GeneralException;

public class CommunityException extends GeneralException {
    public CommunityException(BaseErrorCode code) {
        super(code);
    }
}
