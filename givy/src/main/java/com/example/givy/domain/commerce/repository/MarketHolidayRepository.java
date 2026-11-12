package com.example.givy.domain.commerce.repository;

import com.example.givy.domain.commerce.entity.MarketHoliday;
import com.example.givy.domain.commerce.enums.MarketCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface MarketHolidayRepository extends JpaRepository<MarketHoliday, Long> {
    boolean existsByHolidayDateAndMarketCode(LocalDate holidayDate, MarketCode marketCode);
}
