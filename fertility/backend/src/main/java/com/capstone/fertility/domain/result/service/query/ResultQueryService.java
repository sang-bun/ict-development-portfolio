package com.capstone.fertility.domain.result.service.query;

import com.capstone.fertility.domain.result.dto.res.ResultResDTO;

public interface ResultQueryService {

    /**
     * 파라미터 없음: year=2026, month=이번 달.
     * year만 있으면 해당 year + 이번 달.
     * month만 있으면 2026 + 해당 month.
     * year와 month 둘 다 있으면 해당 연·월.
     */
    ResultResDTO.ResultHistoryDTO getHistory(Long userId, Integer year, Integer month);
}
