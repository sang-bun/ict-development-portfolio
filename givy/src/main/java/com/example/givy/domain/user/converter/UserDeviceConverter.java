package com.example.givy.domain.user.converter;

import com.example.givy.domain.user.entity.UserDeviceToken;
import com.example.givy.domain.user.entity.Users;

public class UserDeviceConverter {

    public static UserDeviceToken toEntity(Users users, String deviceToken, String deviceId){
        return UserDeviceToken.builder()
                .deviceId(deviceId)
                .deviceToken(deviceToken)
                .users(users)
                .build();
    }
}
