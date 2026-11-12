package com.example.givy.domain.user.service.command.device;

public interface UserDeviceCommandService {
    void updateToken(Long userId, String deviceId, String deviceToken);
}
