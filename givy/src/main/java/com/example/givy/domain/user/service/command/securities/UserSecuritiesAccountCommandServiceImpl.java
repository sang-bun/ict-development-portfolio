package com.example.givy.domain.user.service.command.securities;

import com.example.givy.domain.user.code.UserErrorCode;
import com.example.givy.domain.user.converter.UserConverter;
import com.example.givy.domain.user.dto.req.UserSecuritiesReqDTO;
import com.example.givy.domain.user.dto.res.UserSecuritiesResDTO;
import com.example.givy.domain.user.entity.UserSecuritiesAccount;
import com.example.givy.domain.user.entity.Users;
import com.example.givy.domain.user.exception.UserException;
import com.example.givy.domain.user.repository.UserRepository;
import com.example.givy.domain.user.repository.UserSecuritiesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserSecuritiesAccountCommandServiceImpl implements UserSecuritiesAccountCommandService {
    private final UserRepository userRepository;
    private final UserSecuritiesRepository userSecuritiesRepository;

    /* 06-01 증권 계좌 등록 */
    @Override
    public UserSecuritiesResDTO.UserSecuritiesListDTO registerSecurities(Long userId, UserSecuritiesReqDTO.RegisterSecuritiesDTO dto) {
        Users user = userRepository.findById(userId).orElseThrow(() -> new UserException(UserErrorCode.USER_ID_NOT_FOUND));

        List<UserSecuritiesAccount> newSecurities = dto.getSecuritiesList().stream() // DTO 내부 리스트 이름에 맞춰 수정
                .filter(security -> !userSecuritiesRepository.existsByUsers_UserIdAndSecuritiesName(userId, security))
                .map(security -> UserConverter.toUserSecuritiesEntity(user, security))
                .toList();

        if (newSecurities.isEmpty()) {
            return UserConverter.toUserSecuritiesListDTO(Collections.emptyList());
        }

        List<UserSecuritiesAccount> saved = userSecuritiesRepository.saveAll(newSecurities);

        return UserConverter.toUserSecuritiesListDTO(saved);
    }
}
