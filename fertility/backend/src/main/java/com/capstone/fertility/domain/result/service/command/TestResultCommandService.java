package com.capstone.fertility.domain.result.service.command;

import com.capstone.fertility.domain.test.dto.req.TestReqDTO;
import com.capstone.fertility.domain.test.dto.res.TestResDTO;

/**
 * 최종 제출 및 AI 예측 결과 생성 (쓰기/Command 전용 서비스).
 */
public interface TestResultCommandService {

    /**
     * 세션에 대한 최종 제출(PSS 설문 반영) 및 AI 예측 실행 후 결과 저장, SubmitResult 반환.
     *
     * @param userId    인증된 사용자 ID (본인 세션만 허용)
     * @param sessionId 검사 세션 ID
     * @param request   PSS 10문항 답변
     * @return 생성된 결과 DTO
     */
    TestResDTO.SubmitResult submit(Long userId, Long sessionId, TestReqDTO.Submit request);
}

