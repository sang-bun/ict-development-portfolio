package com.example.givy.domain.home.service.query;

import com.example.givy.domain.home.dto.res.HomeResDTO;

public interface HomeQueryService {

    HomeResDTO.HomeResponseDTO getHome(Long userId);
}
