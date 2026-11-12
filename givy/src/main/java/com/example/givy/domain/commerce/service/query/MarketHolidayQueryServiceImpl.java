package com.example.givy.domain.commerce.service.query;

import com.example.givy.domain.commerce.enums.MarketCode;
import com.example.givy.domain.commerce.repository.MarketHolidayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class MarketHolidayQueryServiceImpl implements MarketHolidayQueryService{
    private final MarketHolidayRepository marketHolidayRepository;

    @Override
    public boolean isMarketOpenToday(MarketCode marketCode) {
        //마켓 코드에 따른 현재 날짜
        ZoneId zoneId = (marketCode == MarketCode.KR) ?
                ZoneId.of("Asia/Seoul") : ZoneId.of("America/New_York");

        LocalDate today = LocalDate.now(zoneId);

        //주말
        if(today.getDayOfWeek() == DayOfWeek.SATURDAY || today.getDayOfWeek() == DayOfWeek.SUNDAY) return false;

        //마켓 휴장일 확인
        return !marketHolidayRepository.existsByHolidayDateAndMarketCode(today, marketCode);
    }
}
