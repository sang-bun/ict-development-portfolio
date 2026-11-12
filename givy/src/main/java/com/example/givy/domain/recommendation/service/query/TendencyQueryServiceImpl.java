package com.example.givy.domain.recommendation.service.query;

import com.example.givy.domain.commerce.entity.Product;
import com.example.givy.domain.recommendation.converter.TendencyConverter;
import com.example.givy.domain.recommendation.dto.res.TendencyResDTO;
import com.example.givy.domain.recommendation.entity.RecommendationEvent;
import com.example.givy.domain.recommendation.entity.Tendency;
import com.example.givy.domain.recommendation.exception.TendencyException;
import com.example.givy.domain.recommendation.exception.code.TendencyErrorCode;
import com.example.givy.domain.recommendation.repository.RecommendationEventRepository;
import com.example.givy.domain.recommendation.repository.TendencyRepository;
import com.example.givy.domain.user.code.UserErrorCode;
import com.example.givy.domain.user.entity.Users;
import com.example.givy.domain.user.exception.UserException;
import com.example.givy.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly=true)
public class TendencyQueryServiceImpl implements TendencyQueryService {

    private final TendencyRepository tendencyRepository;
    private final UserRepository userRepository;
    private final RecommendationEventRepository recommendationEventRepository;

    @Override
    public TendencyResDTO.TendencyViewDTO getMyTendency(Long userId) {

        Users user = userRepository.findById(userId).orElseThrow(() -> new UserException(UserErrorCode.USER_ID_NOT_FOUND));

        Tendency tendency = tendencyRepository.findByUsers(user).orElseThrow(() -> new TendencyException(TendencyErrorCode.TENDENCY_NOT_FOUND));

        return TendencyConverter.toTendencyViewDTO(tendency);
    }

    @Override
    public TendencyResDTO.RecommendationListDTO getMyRecommendations(Long userId) {

        Users user = userRepository.findById(userId).orElseThrow(() -> new UserException(UserErrorCode.USER_ID_NOT_FOUND));

        Tendency tendency = tendencyRepository.findByUsers(user).orElseThrow(() -> new TendencyException(TendencyErrorCode.TENDENCY_NOT_FOUND));

        RecommendationEvent event = recommendationEventRepository.findTopByTendencyOrderByCreatedAtDesc(tendency).orElseThrow(() -> new TendencyException(TendencyErrorCode.RECOMMENDATION_NOT_FOUND));

        List<TendencyResDTO.ProductOverviewDTO> productDTO = event.getRecommendationItems().stream().map(
                item -> {
                    Product p = item.getProduct();
                    return TendencyConverter.toProductOverviewDTO(p);
                }).collect(Collectors.toList());

        return TendencyConverter.toRecommendationListDTO(event, tendency, productDTO);
    }


}
