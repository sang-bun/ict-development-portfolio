package com.capstone.fertility.domain.report.exception;

import com.capstone.fertility.global.apiPayLoad.code.BaseErrorCode;
import com.capstone.fertility.global.apiPayLoad.exception.GeneralException;

public class ReportException extends GeneralException {
    public ReportException(BaseErrorCode code) {
        super(code);
    }
}
