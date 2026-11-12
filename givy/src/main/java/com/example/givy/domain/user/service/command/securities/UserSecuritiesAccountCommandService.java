package com.example.givy.domain.user.service.command.securities;

import com.example.givy.domain.user.dto.req.UserSecuritiesReqDTO;
import com.example.givy.domain.user.dto.res.UserSecuritiesResDTO;

public interface UserSecuritiesAccountCommandService {
    UserSecuritiesResDTO.UserSecuritiesListDTO registerSecurities(Long userId, UserSecuritiesReqDTO.RegisterSecuritiesDTO dto);
}
