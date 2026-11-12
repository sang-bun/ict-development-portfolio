package com.example.givy.domain.challenge.service.command;

import com.example.givy.domain.challenge.dto.req.StartChallengeReqDTO;
import com.example.givy.domain.challenge.dto.res.StartChallengeResDTO;

public interface StartChallengeCommandService {
    StartChallengeResDTO.StartChallengeInfoDTO joinChallenge(Long userId, StartChallengeReqDTO.joinChallengeDTO dto);
    StartChallengeResDTO.StartChallengeInfoDTO completeChallenge(Long userId, Long challengeId);
}
