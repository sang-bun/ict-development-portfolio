package com.capstone.fertility.domain.wellnessmission.service;

import com.capstone.fertility.domain.result.repository.TestResultRepository;
import com.capstone.fertility.domain.user.entity.User;
import com.capstone.fertility.domain.wellnessmission.entity.WellnessMission;
import com.capstone.fertility.domain.wellnessmission.entity.WellnessMissionCycleCompletion;
import com.capstone.fertility.domain.wellnessmission.entity.WellnessMissionOfferState;
import com.capstone.fertility.domain.wellnessmission.repository.WellnessMissionCycleCompletionRepository;
import com.capstone.fertility.domain.wellnessmission.repository.WellnessMissionOfferStateRepository;
import com.capstone.fertility.domain.wellnessmission.repository.WellnessMissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * 최신 검사 결과의 웰니스 미션 풀을 사이클 단위로 돌리고, 미완료 미션 중 최대 3개를 슬롯에 채운다.
 */
@Service
@RequiredArgsConstructor
public class WellnessMissionProgressService {

    public record SynchronizedOffer(List<WellnessMission> offered, WellnessMissionOfferState state) {}

    private final WellnessMissionOfferStateRepository offerStateRepository;
    private final WellnessMissionCycleCompletionRepository completionRepository;
    private final TestResultRepository testResultRepository;
    private final WellnessMissionRepository wellnessMissionRepository;

    /**
     * 사이클 전환·슬롯 동기화 후 현재 노출 미션(슬롯 순, 최대 3개)을 반환한다.
     */
    @Transactional
    public SynchronizedOffer synchronizeAndLoadOfferedMissions(User user, Long testResultId, List<WellnessMission> pool) {
        if (pool == null || pool.isEmpty()) {
            return new SynchronizedOffer(List.of(), null);
        }
        WellnessMissionOfferState state = getOrCreateOfferState(user, testResultId);
        advanceCycleIfNeeded(state, pool);
        syncSlotsAndRefill(state, pool);
        offerStateRepository.save(state);
        return new SynchronizedOffer(orderedOfferedMissions(pool, state), state);
    }

    @Transactional
    public void recordCycleCompletion(User user, WellnessMission mission, Long testResultId, int cycleIndex) {
        completionRepository.save(WellnessMissionCycleCompletion.builder()
                .user(user)
                .wellnessMission(mission)
                .testResultId(testResultId)
                .cycleIndex(cycleIndex)
                .completedAt(LocalDateTime.now())
                .build());
    }

    /**
     * 완료 기록 저장 이후 호출: 풀 전체 완료 시 다음 사이클로 넘기고 슬롯을 다시 채운다.
     */
    @Transactional
    public void afterRecordedCompletion(Long userId, Long testResultId, List<WellnessMission> pool) {
        if (pool == null || pool.isEmpty()) {
            return;
        }
        WellnessMissionOfferState state = offerStateRepository.findByUser_IdAndTestResult_Id(userId, testResultId)
                .orElseThrow();
        advanceCycleIfNeeded(state, pool);
        syncSlotsAndRefill(state, pool);
        offerStateRepository.save(state);
    }

    /**
     * 최신 결과 기준 현재 사이클에서 완료된 미션 ID (GET /me 등 읽기 전용 오버레이).
     */
    @Transactional(readOnly = true)
    public Set<Long> completedMissionIdsForLatestResult(Long userId) {
        Long latest = wellnessMissionRepository.findMaxTestResultIdByUserId(userId).orElse(null);
        if (latest == null) {
            return Set.of();
        }
        int cycle = offerStateRepository.findByUser_IdAndTestResult_Id(userId, latest)
                .map(WellnessMissionOfferState::getCycleIndex)
                .orElse(0);
        return completionRepository.findCompletedMissionIds(userId, latest, cycle);
    }

    @Transactional(readOnly = true)
    public WellnessMissionOfferState getOfferStateOrNull(Long userId, Long testResultId) {
        return offerStateRepository.findByUser_IdAndTestResult_Id(userId, testResultId).orElse(null);
    }

    private WellnessMissionOfferState getOrCreateOfferState(User user, Long testResultId) {
        return offerStateRepository.findByUser_IdAndTestResult_Id(user.getId(), testResultId)
                .orElseGet(() -> offerStateRepository.save(WellnessMissionOfferState.builder()
                        .user(user)
                        .testResult(testResultRepository.getReferenceById(testResultId))
                        .cycleIndex(0)
                        .build()));
    }

    private void advanceCycleIfNeeded(WellnessMissionOfferState state, List<WellnessMission> pool) {
        if (pool.isEmpty()) {
            return;
        }
        int c = state.getCycleIndex();
        long done = completionRepository.countByUser_IdAndTestResultIdAndCycleIndex(
                state.getUser().getId(), state.getTestResult().getId(), c);
        if (done >= pool.size()) {
            state.setCycleIndex(c + 1);
            state.clearAllSlots();
        }
    }

    private void syncSlotsAndRefill(WellnessMissionOfferState state, List<WellnessMission> pool) {
        Long userId = state.getUser().getId();
        Long resultId = state.getTestResult().getId();
        Set<Long> poolIds = pool.stream().map(WellnessMission::getId).collect(Collectors.toSet());
        Set<Long> completed = completionRepository.findCompletedMissionIds(userId, resultId, state.getCycleIndex());

        sanitizeAndClearStaleSlots(state, poolIds, completed);
        refillEmptySlots(state, poolIds, completed);
    }

    private void sanitizeAndClearStaleSlots(WellnessMissionOfferState state, Set<Long> poolIds, Set<Long> completed) {
        if (state.getSlot1MissionId() != null
                && (!poolIds.contains(state.getSlot1MissionId()) || completed.contains(state.getSlot1MissionId()))) {
            state.setSlot1MissionId(null);
        }
        if (state.getSlot2MissionId() != null
                && (!poolIds.contains(state.getSlot2MissionId()) || completed.contains(state.getSlot2MissionId()))) {
            state.setSlot2MissionId(null);
        }
        if (state.getSlot3MissionId() != null
                && (!poolIds.contains(state.getSlot3MissionId()) || completed.contains(state.getSlot3MissionId()))) {
            state.setSlot3MissionId(null);
        }
    }

    private void refillEmptySlots(WellnessMissionOfferState state, Set<Long> poolIds, Set<Long> completed) {
        Set<Long> incomplete = poolIds.stream()
                .filter(id -> !completed.contains(id))
                .collect(Collectors.toCollection(HashSet::new));
        ThreadLocalRandom r = ThreadLocalRandom.current();
        while (countNonNullSlots(state) < 3 && !incomplete.isEmpty()) {
            Set<Long> used = currentSlotMissionIds(state);
            List<Long> candidates = incomplete.stream().filter(id -> !used.contains(id)).toList();
            if (candidates.isEmpty()) {
                break;
            }
            Long pick = candidates.get(r.nextInt(candidates.size()));
            assignFirstEmptySlot(state, pick);
        }
    }

    private static int countNonNullSlots(WellnessMissionOfferState state) {
        int n = 0;
        if (state.getSlot1MissionId() != null) {
            n++;
        }
        if (state.getSlot2MissionId() != null) {
            n++;
        }
        if (state.getSlot3MissionId() != null) {
            n++;
        }
        return n;
    }

    private static Set<Long> currentSlotMissionIds(WellnessMissionOfferState state) {
        Set<Long> used = new HashSet<>();
        if (state.getSlot1MissionId() != null) {
            used.add(state.getSlot1MissionId());
        }
        if (state.getSlot2MissionId() != null) {
            used.add(state.getSlot2MissionId());
        }
        if (state.getSlot3MissionId() != null) {
            used.add(state.getSlot3MissionId());
        }
        return used;
    }

    private static void assignFirstEmptySlot(WellnessMissionOfferState state, Long id) {
        if (state.getSlot1MissionId() == null) {
            state.setSlot1MissionId(id);
        } else if (state.getSlot2MissionId() == null) {
            state.setSlot2MissionId(id);
        } else if (state.getSlot3MissionId() == null) {
            state.setSlot3MissionId(id);
        }
    }

    private static List<WellnessMission> orderedOfferedMissions(List<WellnessMission> pool, WellnessMissionOfferState state) {
        Map<Long, WellnessMission> byId = pool.stream().collect(Collectors.toMap(
                WellnessMission::getId,
                m -> m,
                (a, b) -> a,
                LinkedHashMap::new));
        List<WellnessMission> out = new ArrayList<>();
        addIfPresent(out, byId, state.getSlot1MissionId());
        addIfPresent(out, byId, state.getSlot2MissionId());
        addIfPresent(out, byId, state.getSlot3MissionId());
        return out;
    }

    private static void addIfPresent(List<WellnessMission> out, Map<Long, WellnessMission> byId, Long id) {
        if (id == null) {
            return;
        }
        WellnessMission m = byId.get(id);
        if (m != null) {
            out.add(m);
        }
    }
}
