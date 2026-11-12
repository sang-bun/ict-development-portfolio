package com.capstone.fertility.domain.user.service.command;

import com.capstone.fertility.domain.user.dto.req.UserReqDTO;
import com.capstone.fertility.domain.user.dto.res.UserResDTO;

public interface UserCommandService {

    UserResDTO.LoginResDTO signUp(UserReqDTO.SignUpReqDTO request);

    UserResDTO.UserInfoDTO updateMyInfo(Long userId, UserReqDTO.UpdateProfileDTO request);

    UserResDTO.LoginResDTO login(UserReqDTO.LoginReqDTO request);

    void withdraw(Long userId);
}