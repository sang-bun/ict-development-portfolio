package com.capstone.fertility.domain.test.service.query;

import com.capstone.fertility.domain.test.dto.res.TestResDTO;

public interface TestQueryService {

    /**
     * 검사 세션 상세 조회. 임시 저장된 currentStep과 입력값을 반환하며, 본인 세션만 조회 가능.
     */
    TestResDTO.SessionDetailDTO getSession(Long userId, Long sessionId);

    /**
     * 중간 보고서 조회(사용자 입력값 기반 분석).
     * - 수면: age 10년 단위 평균 대비 차이
     * - 비만: BMI(키 cm 기준) + 1단계 비만 유병률 비교
     */
    TestResDTO.InterimReportDTO getInterimReport(Long userId, Long sessionId);
}
