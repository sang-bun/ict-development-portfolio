package com.example.givy.domain.challenge.service.query;

import com.example.givy.domain.challenge.code.StartChallengeErrorCode;
import com.example.givy.domain.challenge.converter.StartChallengeConverter;
import com.example.givy.domain.challenge.dto.res.StartChallengeResDTO;
import com.example.givy.domain.challenge.entity.StartChallenge;
import com.example.givy.domain.challenge.enums.Status;
import com.example.givy.domain.challenge.exception.StartChallengeException;
import com.example.givy.domain.challenge.repository.StartChallengeRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StartChallengeQueryServiceImpl implements StartChallengeQueryService{
    private final StartChallengeRepository startChallengeRepository;

    @Override
    public List<StartChallengeResDTO.StartChallengeStatusDTO> getChallengeStatus(Long userId) {
        List<StartChallenge> allByUserId = startChallengeRepository.findAllByUsers_UserId(userId);

        return allByUserId.stream()
                .map(startChallenge -> StartChallengeConverter.toStartChallengeStatusDTO(startChallenge))
                .toList();
    }
}
