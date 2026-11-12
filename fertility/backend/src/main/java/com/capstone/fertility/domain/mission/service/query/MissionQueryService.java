package com.capstone.fertility.domain.mission.service.query;

import com.capstone.fertility.domain.mission.dto.res.MissionResDTO;

import java.util.List;

public interface MissionQueryService {

    MissionResDTO.MissionHistoryDTO getHistory(Long userId, Long lastLogId, int size);

    List<MissionResDTO.FlowerCollectionItemDTO> getCollections(Long userId);
}
