package com.capstone.fertility.domain.user.service.query;

import com.capstone.fertility.domain.user.converter.UserConverter;
import com.capstone.fertility.domain.user.dto.res.UserResDTO;
import com.capstone.fertility.domain.user.entity.User;
import com.capstone.fertility.domain.user.exception.UserException;
import com.capstone.fertility.domain.user.exception.code.UserErrorCode;
import com.capstone.fertility.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryServiceImpl implements UserQueryService {

    private final UserRepository userRepository;

    @Override
    public UserResDTO.UserInfoDTO getMyInfo(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException(UserErrorCode.USER_ID_NOT_FOUND));
        if (!user.isActive()) {
            throw new UserException(UserErrorCode.USER_WITHDRAWN);
        }

        return UserConverter.toUserInfoDTO(user);
    }
}
