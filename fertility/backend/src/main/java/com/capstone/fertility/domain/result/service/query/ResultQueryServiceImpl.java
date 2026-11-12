package com.capstone.fertility.domain.result.service.query;

import com.capstone.fertility.domain.result.converter.ResultConverter;
import com.capstone.fertility.domain.result.dto.res.ResultResDTO;
import com.capstone.fertility.domain.result.entity.TestResult;
import com.capstone.fertility.domain.result.exception.ResultException;
import com.capstone.fertility.domain.result.exception.code.ResultErrorCode;
import com.capstone.fertility.domain.result.repository.TestResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ResultQueryServiceImpl implements ResultQueryService {

    private static final int MIN_YEAR = 2000;
    private static final int MAX_YEAR = 2100;

    private final TestResultRepository testResultRepository;

    @Override
    public ResultResDTO.ResultHistoryDTO getHistory(Long userId, Integer year, Integer month) {
        int targetYear;
        int targetMonth;

        // 파라미터 누락 허용 정책: 서버 시각(KST 기본 JVM TZ) 기준 현재 연/월로 조회.
        LocalDate today = LocalDate.now();
        targetYear = (year == null) ? today.getYear() : year;
        targetMonth = (month == null) ? today.getMonthValue() : month;

        validateYearMonth(targetYear, targetMonth);

        LocalDateTime startInclusive = LocalDateTime.of(LocalDate.of(targetYear, targetMonth, 1), LocalTime.MIN);
        LocalDateTime endExclusive = startInclusive.plusMonths(1);

        List<TestResult> rows = testResultRepository.findByUserIdAndCreatedAtInMonth(
                userId, startInclusive, endExclusive);

        List<ResultResDTO.ResultHistoryItemDTO> items = rows.stream()
                .map(ResultConverter::toHistoryItemDTO)
                .toList();

        return ResultResDTO.ResultHistoryDTO.builder()
                .year(targetYear)
                .month(targetMonth)
                .items(items)
                .build();
    }

    private void validateYearMonth(int year, int month) {
        if (year < MIN_YEAR || year > MAX_YEAR) {
            throw new ResultException(ResultErrorCode.INVALID_YEAR_FOR_HISTORY);
        }
        if (month < 1 || month > 12) {
            throw new ResultException(ResultErrorCode.INVALID_MONTH_FOR_HISTORY);
        }
    }
}
