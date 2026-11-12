package com.capstone.fertility.domain.mission.service.query;

import com.capstone.fertility.domain.mission.dto.res.MissionResDTO;
import com.capstone.fertility.domain.mission.entity.MissionSproutLog;
import com.capstone.fertility.domain.mission.entity.UserFlowerCollection;
import com.capstone.fertility.domain.mission.repository.MissionSproutLogRepository;
import com.capstone.fertility.domain.mission.repository.UserFlowerCollectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MissionQueryServiceImpl implements MissionQueryService {

    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;
    private static final DateTimeFormatter ISO_INSTANT_STYLE = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final MissionSproutLogRepository missionSproutLogRepository;
    private final UserFlowerCollectionRepository userFlowerCollectionRepository;

    @Override
    public MissionResDTO.MissionHistoryDTO getHistory(Long userId, Long lastLogId, int size) {
        int pageSize = normalizeSize(size);
        var pageable = PageRequest.of(0, pageSize);

        List<MissionSproutLog> rows = (lastLogId == null)
                ? missionSproutLogRepository.findByUser_IdOrderByIdDesc(userId, pageable)
                : missionSproutLogRepository.findByUser_IdAndIdLessThanOrderByIdDesc(userId, lastLogId, pageable);

        List<MissionResDTO.MissionHistoryItemDTO> items = new ArrayList<>(rows.size());
        for (MissionSproutLog row : rows) {
            items.add(MissionResDTO.MissionHistoryItemDTO.builder()
                    .date(row.getCreatedAt() != null ? ISO_INSTANT_STYLE.format(row.getCreatedAt()) : null)
                    .action(row.getAction().name())
                    .expChange(formatExpChange(row.getExpDelta()))
                    .build());
        }

        Long nextLastLogId = null;
        if (!rows.isEmpty() && rows.size() == pageSize) {
            nextLastLogId = rows.get(rows.size() - 1).getId();
        }

        return MissionResDTO.MissionHistoryDTO.builder()
                .items(items)
                .nextLastLogId(nextLastLogId)
                .build();
    }

    @Override
    public List<MissionResDTO.FlowerCollectionItemDTO> getCollections(Long userId) {
        List<UserFlowerCollection> rows = userFlowerCollectionRepository.findByUser_IdOrderByAchievedAtDesc(userId);
        List<MissionResDTO.FlowerCollectionItemDTO> items = new ArrayList<>(rows.size());
        for (UserFlowerCollection row : rows) {
            items.add(MissionResDTO.FlowerCollectionItemDTO.builder()
                    .flowerType(row.getFlowerType().name())
                    .achievedAt(row.getAchievedAt() != null ? ISO_INSTANT_STYLE.format(row.getAchievedAt()) : null)
                    .build());
        }
        return items;
    }

    private static int normalizeSize(int size) {
        if (size <= 0) {
            return DEFAULT_SIZE;
        }
        return Math.min(size, MAX_SIZE);
    }

    static String formatExpChange(int delta) {
        if (delta > 0) {
            return "+" + delta;
        }
        return Integer.toString(delta);
    }
}
