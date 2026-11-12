package com.example.givy.domain.notification.service.command;

import com.example.givy.domain.commerce.enums.MarketCode;

public interface NotificationCommandService {
    public void sendMarketOpenPush(MarketCode marketCode);
}
