package com.example.givy.domain.challenge.service.query;

import com.example.givy.domain.challenge.dto.res.StartChallengeResDTO;

import java.util.List;

public interface StartChallengeQueryService {
    List<StartChallengeResDTO.StartChallengeStatusDTO> getChallengeStatus(Long userId);
}
