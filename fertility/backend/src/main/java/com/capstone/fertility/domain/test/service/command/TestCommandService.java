package com.capstone.fertility.domain.test.service.command;

import com.capstone.fertility.domain.test.dto.req.TestReqDTO;
import com.capstone.fertility.domain.test.dto.res.TestResDTO;

public interface TestCommandService {

    TestResDTO.CreateSessionResDTO start(Long userId, TestReqDTO.Start request);

    void saveMaleStep(Long userId, Long sessionId, TestReqDTO.MaleStepSave request);

    void saveFemaleStep(Long userId, Long sessionId, TestReqDTO.FemaleStepSave request);
}
