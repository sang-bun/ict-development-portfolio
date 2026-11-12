package com.example.givy.domain.user.service.query;

import com.example.givy.domain.user.code.UserErrorCode;
import com.example.givy.domain.user.converter.UserConverter;
import com.example.givy.domain.user.dto.res.UserResDTO;
import com.example.givy.domain.user.entity.Users;
import com.example.givy.domain.user.exception.UserException;
import com.example.givy.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryServiceImpl implements UserQueryService {

    private final UserRepository userRepository;

    /* 01-05 내 정보 조회 API */
    @Override
    public UserResDTO.UserMyPageDTO getUserInfo(Long userId) {

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_ID_NOT_FOUND));

        return UserConverter.toMyPageDTO(user);

    }

    /* 06-02 성인 여부 판단 */
    @Override
    public UserResDTO.AgeVerificationResDTO verifyAdult(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_ID_NOT_FOUND));

        boolean isAdult;
        int diff = LocalDate.now().getYear() - user.getBirth().getYear();

        if (diff >= 19) isAdult = true;
        else isAdult = false;

        return UserConverter.toAgeVerificationResDTO(user, isAdult);
    }
}
