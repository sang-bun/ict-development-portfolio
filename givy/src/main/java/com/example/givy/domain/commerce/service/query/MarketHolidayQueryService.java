package com.example.givy.domain.commerce.service.query;

import com.example.givy.domain.commerce.enums.MarketCode;

public interface MarketHolidayQueryService {
    boolean isMarketOpenToday(MarketCode marketCode);
}
