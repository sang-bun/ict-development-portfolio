package com.capstone.fertility.domain.user.service.query;

import com.capstone.fertility.domain.user.dto.res.UserResDTO;

public interface UserQueryService {
    UserResDTO.UserInfoDTO getMyInfo(Long userId);
}
