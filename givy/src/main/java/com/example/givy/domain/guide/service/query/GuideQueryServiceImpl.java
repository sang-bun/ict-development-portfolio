package com.example.givy.domain.guide.service.query;

import com.example.givy.domain.guide.code.GuideErrorCode;
import com.example.givy.domain.guide.converter.GuideConverter;
import com.example.givy.domain.guide.dto.res.GuideResDTO;
import com.example.givy.domain.guide.entity.Guide;
import com.example.givy.domain.guide.enums.GuideCategory;
import com.example.givy.domain.guide.exception.GuideException;
import com.example.givy.domain.guide.repository.CustomGuideRepository;
import com.example.givy.domain.guide.repository.GuideLikeRepository;
import com.example.givy.domain.guide.repository.GuideRepository;
import com.example.givy.domain.guide.repository.GuideStoreRespository;
import com.example.givy.domain.user.code.UserErrorCode;
import com.example.givy.domain.user.entity.Users;
import com.example.givy.domain.user.exception.UserException;
import com.example.givy.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly=true)
public class GuideQueryServiceImpl implements GuideQueryService {

    private final GuideRepository guideRepository;
    private final GuideLikeRepository guideLikeRepository;
    private final GuideStoreRespository guideStoreRespository;
    private final CustomGuideRepository customGuideRepository;
    private final UserRepository userRepository;


    /* 04-02 가이드 상세 조회 */
    @Override
    public GuideResDTO.GuideDetailDTO getGuideDetail(Long guideId, Long userId) {

        Guide guide = guideRepository.findById(guideId)
                .orElseThrow(() -> new GuideException(GuideErrorCode.GUIDE_NOT_FOUND));

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_ID_NOT_FOUND));

        boolean isLiked = guideLikeRepository
                .existsByGuideAndUsersAndIsDeletedFalse(guide, user);

        boolean isStored = guideStoreRespository
                .existsByGuideAndUsersAndIsDeletedFalse(guide, user);

        return GuideConverter.toDetailDTO(guide, isLiked, isStored);
    }

    /* 04-05 내가 저장한 가이드 조회 */
    @Override
    public List<GuideResDTO.GuideSummaryDTO> getMyStoredGuides(Long userId) {

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_ID_NOT_FOUND));

        return guideStoreRespository.findMyStoredGuides(user)
                .stream()
                .map(guideStore -> GuideConverter.toSummaryDTO(guideStore.getGuide()))
                .toList();
    }

    /* 04-01, 04-06 가이드 목록 조회(카테고리 선택형, Slice) */
    @Override
    public Slice<GuideResDTO.GuideSummaryDTO> getGuides(
            String category, Pageable pageable) {
        GuideCategory guideCategory = GuideCategory.from(category);
        return customGuideRepository.findGuides(guideCategory, pageable);
    }
}
