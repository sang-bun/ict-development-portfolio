package com.capstone.fertility.domain.report.service;

import com.capstone.fertility.domain.report.dto.res.ReportResDTO;

public interface ReportService {

    ReportResDTO.DetailReport generateReport(Long userId, Long resultId);
}
