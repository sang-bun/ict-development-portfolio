package com.capstone.fertility.domain.wellnessmission.service.query;

import com.capstone.fertility.domain.user.entity.User;
import com.capstone.fertility.domain.user.exception.UserException;
import com.capstone.fertility.domain.user.exception.code.UserErrorCode;
import com.capstone.fertility.domain.user.repository.UserRepository;
import com.capstone.fertility.domain.wellnessmission.dto.res.WellnessMissionResDTO;
import com.capstone.fertility.domain.wellnessmission.entity.WellnessMission;
import com.capstone.fertility.domain.wellnessmission.repository.WellnessMissionRepository;
import com.capstone.fertility.domain.wellnessmission.service.WellnessMissionProgressService;
import com.capstone.fertility.domain.wellnessmission.support.WellnessMissionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WellnessMissionQueryServiceImpl implements WellnessMissionQueryService {

    private final WellnessMissionRepository wellnessMissionRepository;
    private final WellnessMissionProgressService wellnessMissionProgressService;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public WellnessMissionResDTO.MyMissions getMyMissions(Long userId) {
        List<WellnessMission> missions = wellnessMissionRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
        Long latestResultId = wellnessMissionRepository.findMaxTestResultIdByUserId(userId).orElse(null);
        Set<Long> completedInActiveCycle = wellnessMissionProgressService.completedMissionIdsForLatestResult(userId);
        List<WellnessMissionResDTO.MissionItem> items = missions.stream()
                .map(m -> {
                    boolean done = latestResultId != null
                            && m.getTestResult() != null
                            && latestResultId.equals(m.getTestResult().getId())
                            && completedInActiveCycle.contains(m.getId());
                    return WellnessMissionMapper.toItem(m, done, null);
                })
                .collect(Collectors.toList());
        return WellnessMissionResDTO.MyMissions.builder()
                .total(items.size())
                .missions(items)
                .build();
    }

    @Override
    @Transactional
    public WellnessMissionResDTO.MyMissions getTodayMissions(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_ID_NOT_FOUND));

        Long latestResultId = wellnessMissionRepository.findMaxTestResultIdByUserId(userId).orElse(null);
        if (latestResultId == null) {
            return WellnessMissionResDTO.MyMissions.builder()
                    .total(0)
                    .missions(List.of())
                    .build();
        }
        List<WellnessMission> pool = wellnessMissionRepository.findByUser_IdAndTestResult_IdOrderByIdAsc(userId, latestResultId);
        WellnessMissionProgressService.SynchronizedOffer snap =
                wellnessMissionProgressService.synchronizeAndLoadOfferedMissions(user, latestResultId, pool);
        List<WellnessMissionResDTO.MissionItem> items = snap.offered().stream()
                .map(m -> WellnessMissionMapper.toItem(m, false, null))
                .collect(Collectors.toList());
        return WellnessMissionResDTO.MyMissions.builder()
                .total(items.size())
                .missions(items)
                .build();
    }
}
