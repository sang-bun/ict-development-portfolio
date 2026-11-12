package com.example.givy.domain.commerce.entity;

import com.example.givy.domain.commerce.enums.MarketCode;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name="market_holiday")
public class MarketHoliday {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="market_holiday_id")
    private Long marketHolidayId;

    @Column(name="holiday_date", nullable = false)
    private LocalDate holidayDate;

    //ex. 주말, 공휴일
    @Column(name="name", nullable = false)
    private String name;

    //ex. 한국, 미국
    @Enumerated(EnumType.STRING)
    @Column(name="market_code", nullable = false)
    private MarketCode marketCode;

    @Column(name="created_at", updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

}
