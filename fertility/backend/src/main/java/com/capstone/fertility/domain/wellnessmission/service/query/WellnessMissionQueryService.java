package com.capstone.fertility.domain.wellnessmission.service.query;

import com.capstone.fertility.domain.wellnessmission.dto.res.WellnessMissionResDTO;

public interface WellnessMissionQueryService {
    WellnessMissionResDTO.MyMissions getMyMissions(Long userId);

    /** 최신 검사 기준 오늘(KST)의 웰니스 미션 3개(자정 리셋 반영). */
    WellnessMissionResDTO.MyMissions getTodayMissions(Long userId);
}
