package umc.fitty.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.fitty.config.security.SecurityUtil;
import umc.fitty.domain.Goal;
import umc.fitty.domain.RunRecord;
import umc.fitty.domain.User;
import umc.fitty.domain.enums.GoalType;
import umc.fitty.repository.GoalRepository;
import umc.fitty.repository.RecordRepository;
import umc.fitty.repository.UserRepository;
import umc.fitty.web.dto.GoalDTO.DayStatusDTO;
import umc.fitty.web.dto.GoalDTO.GoalCreateRequestDTO;
import umc.fitty.web.dto.GoalDTO.GoalDetailResponseDTO;
import umc.fitty.web.dto.GoalDTO.GoalResponseDTO;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GoalService {
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final RecordRepository recordRepository;

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    @Transactional
    public GoalResponseDTO create(Long currentUserId, GoalCreateRequestDTO req){
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        LocalDate weekStart = resolveWeekStart();

        // 이미 존재하면 409 또는 덮어쓰기(택1). 여기서는 409.
        goalRepository.findByUserIdAndWeekStartDate(currentUserId, weekStart)
                .ifPresent(g -> { throw new IllegalStateException("이미 해당 주 목표가 존재합니다."); });

        validateByType(req); // 한 타입만 set

        Goal goal = Goal.builder()
                .user(user)
                .type(req.getType())
                .weekStartDate(weekStart)
                .targetCount( req.getType()==GoalType.COUNT    ? req.getTargetCount()    : null)
                .targetDistance(req.getType()==GoalType.DISTANCE ? req.getTargetDistance() : null)
                .targetTime(  req.getType()==GoalType.DURATION ? req.getTargetTime()     : null)
                .progressCount(0)
                .progressDistance(0d)
                .progressTime(0)
                .build();

        Goal saved = goalRepository.save(goal);
        return toResponse(saved);
    }

    private void validateByType(GoalCreateRequestDTO r){
        int filled = (r.getTargetCount()!=null?1:0) + (r.getTargetDistance()!=null?1:0) + (r.getTargetTime()!=null?1:0);
        if (filled != 1) throw new IllegalArgumentException("타입에 맞는 target 하나만 입력해야 합니다.");

        switch (r.getType()){
            case COUNT    -> require(r.getTargetCount()!=null && r.getTargetCount()>=0, "targetCount >= 0");
            case DISTANCE -> require(r.getTargetDistance()!=null && r.getTargetDistance()>=0, "targetDistance >= 0");
            case DURATION -> require(r.getTargetTime()!=null && r.getTargetTime()>=0, "targetTime >= 0");
        }
    }

    private void require(boolean cond, String msg){ if(!cond) throw new IllegalArgumentException(msg); }

    private LocalDate resolveWeekStart() {
        return LocalDate.now(KST)
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    private GoalResponseDTO toResponse(Goal g){
        return GoalResponseDTO.builder()
                .id(g.getId())
                .type(g.getType())
                .targetCount(g.getTargetCount())
                .targetDistance(g.getTargetDistance())
                .targetTime(g.getTargetTime())
                .progressCount(g.getProgressCount())
                .progressDistance(g.getProgressDistance())
                .progressTime(g.getProgressTime())
                .weekStartDate(g.getWeekStartDate())
                .summary(g.summary())
                .build();
    }

    public GoalDetailResponseDTO getCurrentWeek() {
        Long userId = SecurityUtil.getCurrentUserId();

        LocalDate today = LocalDate.now(KST);
        LocalDate weekStart = mondayOf(today);
        LocalDate weekEnd = weekStart.plusDays(6);

        Goal goal = goalRepository.findByUserIdAndWeekStartDate(userId, weekStart)
                .orElseThrow(() -> new IllegalArgumentException("이번 주 목표가 없습니다."));

        // createdAt 기준 주간 범위 조회 (00:00:00 ~ 23:59:59.999999999)
        LocalDateTime startDt = weekStart.atStartOfDay();
        LocalDateTime endDt = weekEnd.plusDays(1).atStartOfDay().minusNanos(1);

        List<RunRecord> records = recordRepository
                .findAllByGoalIdAndCreatedAtBetween(goal.getId(), startDt, endDt);

        // 날짜별 기록 개수 (createdAt -> LocalDate)
        Map<LocalDate, Long> countByDate = records.stream()
                .collect(Collectors.groupingBy(r -> r.getCreatedAt().toLocalDate(),
                        Collectors.counting()));

        String[] labels = {"월","화","수","목","금","토","일"};

        List<DayStatusDTO> days = IntStream.range(0, 7)
                .mapToObj(i -> {
                    LocalDate d = weekStart.plusDays(i);
                    int runCount = countByDate.getOrDefault(d, 0L).intValue();
                    String status;
                    if (d.isAfter(today)) status = "FUTURE";
                    else status = (runCount > 0) ? "DONE" : "MISS";
                    return DayStatusDTO.builder()
                            .dayLabel(labels[i])
                            .date(d)
                            .status(status)
                            .runCount(runCount)
                            .build();
                }).toList();

        String summary = switch (goal.getType()){
            case COUNT    -> "일주일간 " + nz(goal.getTargetCount())    + "번 달리기";
            case DISTANCE -> "일주일간 " + nz(goal.getTargetDistance()) + "km 달리기";
            case DURATION -> "일주일간 " + nz(goal.getTargetTime())     + "분 달리기";
        };

        String achievement = switch (goal.getType()){
            case COUNT    -> String.format("이번 주 러닝 목표 중 %d / %d번 달성!",
                    nz(goal.getProgressCount()), nz(goal.getTargetCount()));
            case DISTANCE -> String.format("이번 주 러닝 목표 중 %.0f / %.0fkm 달성!",
                    nz(goal.getProgressDistance()), nz(goal.getTargetDistance()));
            case DURATION -> String.format("이번 주 러닝 목표 중 %d / %d분 달성!",
                    nz(goal.getProgressTime()), nz(goal.getTargetTime()));
        };

        return GoalDetailResponseDTO.builder()
                .id(goal.getId())
                .type(goal.getType())
                .targetCount(goal.getTargetCount())
                .progressCount(goal.getProgressCount())
                .targetDistance(goal.getTargetDistance())
                .progressDistance(goal.getProgressDistance())
                .targetTime(goal.getTargetTime())
                .progressTime(goal.getProgressTime())
                .weekStartDate(weekStart)
                .weekEndDate(weekEnd)
                .summary(summary)
                .achievement(achievement)
                .days(days)
                .build();
    }

    private LocalDate mondayOf(LocalDate base) {
        return base.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }
    private int nz(Integer v){ return v==null?0:v; }
    private double nz(Double v){ return v==null?0d:v; }
}
