package com.example.givy.domain.guide.service.command;

public interface GuideCommandService {
    void toggleLike(Long guideId, Long userId);

    /* 04-04 가이드 단건 저장 API */
    void toggleStore(Long guideId, Long userId);
}
