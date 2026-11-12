package com.capstone.fertility.domain.wellnessmission.service.command;

import com.capstone.fertility.domain.wellnessmission.dto.res.WellnessMissionResDTO;

public interface WellnessMissionCommandService {

    WellnessMissionResDTO.CompleteResult complete(Long userId, Long missionId);
}
