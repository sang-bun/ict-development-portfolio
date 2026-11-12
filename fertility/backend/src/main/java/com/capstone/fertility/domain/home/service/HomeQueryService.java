package com.capstone.fertility.domain.home.service;

import com.capstone.fertility.domain.home.dto.res.HomeResDTO;

public interface HomeQueryService {

    /**
     * 홈 화면 데이터 조회. user(nickname, level, exp)와
     * todayMissions(최신 검사 기준 오늘의 미션 3개)를 채워준다.
     * recentTest / unreadNotiCount는 추후 연동(현재 null·0).
     */
    HomeResDTO.HomeDTO getHome(Long userId);
}
