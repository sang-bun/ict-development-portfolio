package com.example.givy.domain.user.service.command.device;

import com.example.givy.domain.user.code.UserErrorCode;
import com.example.givy.domain.user.converter.UserDeviceConverter;
import com.example.givy.domain.user.entity.UserDeviceToken;
import com.example.givy.domain.user.entity.Users;
import com.example.givy.domain.user.exception.UserException;
import com.example.givy.domain.user.repository.UserDeviceRepository;
import com.example.givy.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserDeviceCommandServiceImpl implements UserDeviceCommandService {
    private final UserDeviceRepository userDeviceRepository;
    private final UserRepository userRepository;

    /* 로그인 시 기기 토큰과 고유 Id 등록 */
    @Override
    public void updateToken(Long userId, String deviceId, String deviceToken) {
        Optional<UserDeviceToken> userIdAndDeviceId = userDeviceRepository.findByUsers_userIdAndDeviceId(userId, deviceId);

        if(userIdAndDeviceId.isPresent()){
            userIdAndDeviceId.get().updateToken(deviceToken);
            System.out.println("[deviceToken 업데이트 완료] deviceToken = " + deviceToken);
        } else{
            Users users = userRepository.findById(userId)
                    .orElseThrow(() -> new UserException(UserErrorCode.USER_ID_NOT_FOUND));

            UserDeviceToken entity = UserDeviceConverter.toEntity(users, deviceToken, deviceId);

            userDeviceRepository.save(entity);
            System.out.println("[deviceToken 등록 완료] deviceToken = " + deviceToken);
        }
    }
}
