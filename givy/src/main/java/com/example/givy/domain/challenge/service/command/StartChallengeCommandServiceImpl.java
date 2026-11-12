package com.example.givy.domain.challenge.service.command;

import com.example.givy.domain.challenge.code.StampErrorCode;
import com.example.givy.domain.challenge.code.StartChallengeErrorCode;
import com.example.givy.domain.challenge.converter.StartChallengeConverter;
import com.example.givy.domain.challenge.dto.req.StartChallengeReqDTO;
import com.example.givy.domain.challenge.dto.res.StartChallengeResDTO;
import com.example.givy.domain.challenge.entity.Stamp;
import com.example.givy.domain.challenge.entity.StartChallenge;
import com.example.givy.domain.challenge.enums.Status;
import com.example.givy.domain.challenge.exception.StampException;
import com.example.givy.domain.challenge.exception.StartChallengeException;
import com.example.givy.domain.challenge.repository.StampRepository;
import com.example.givy.domain.challenge.repository.StartChallengeRepository;
import com.example.givy.domain.commerce.code.ProductErrorCode;
import com.example.givy.domain.commerce.entity.Product;
import com.example.givy.domain.commerce.exception.ProductException;
import com.example.givy.domain.commerce.repository.ProductRepository;
import com.example.givy.domain.user.code.UserErrorCode;
import com.example.givy.domain.user.converter.UserConverter;
import com.example.givy.domain.user.entity.UserStamp;
import com.example.givy.domain.user.entity.Users;
import com.example.givy.domain.user.exception.UserException;
import com.example.givy.domain.user.repository.UserRepository;
import com.example.givy.domain.user.repository.UserStampRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class StartChallengeCommandServiceImpl implements StartChallengeCommandService {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final StartChallengeRepository startChallengeRepository;
    private final StampRepository stampRepository;
    private final UserStampRepository userStampRepository;

    @Override
    public StartChallengeResDTO.StartChallengeInfoDTO joinChallenge(Long userId, StartChallengeReqDTO.joinChallengeDTO dto) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_ID_NOT_FOUND));

        Product product = productRepository.findByProductId(dto.getProductId())
                .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND_BY_PRODUCT_ID));

        //이미 참여 중인 챌린지가 있는 경우 throw
        if(startChallengeRepository.existsByUsers_userIdAndStatus(userId, Status.IN_PROGRESS)){
            throw new StartChallengeException(StartChallengeErrorCode.CHALLENGE_ALREADY_PROGRESSING);
        }

        //해당 상품에 참여한 전적이 있는 경우 throw
        if(startChallengeRepository.existsByUsers_UserIdAndProduct_ProductId(userId, product.getProductId())){
            throw new StartChallengeException(StartChallengeErrorCode.CHALLENGE_ALREADY_EXISTS);
        }

        StartChallenge challenge = StartChallengeConverter.toEntity(user, product);

        startChallengeRepository.save(challenge);

        return StartChallengeConverter.toStartChallengeInfoDTO(challenge);
    }

    @Override
    @Transactional
    public StartChallengeResDTO.StartChallengeInfoDTO completeChallenge(Long userId, Long challengeId) {
        StartChallenge startChallenge = startChallengeRepository.findByStartChallengeIdAndUsers_UserId(challengeId, userId)
                .orElseThrow(() -> new StartChallengeException(StartChallengeErrorCode.USER_CHALLENGE_NOT_FOUND));

        //이미 완료한 경우
        if (startChallenge.getStatus().equals(Status.COMPLETED)) {
            return null;
        }

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_ID_NOT_FOUND));

        //사용자 스탬프 부여
        Stamp stamp = stampRepository.findByName("스타트 챌린지 완료")
                .orElseThrow(() -> new StampException(StampErrorCode.STAMP_NOT_FOUND_BY_NAME));

        //스탬프 중복 부여 방지
        if (!userStampRepository.existsByUsers_UserId(userId)) {
            UserStamp userStamp = UserConverter.toUserStampEntity(user, stamp);
            userStampRepository.save(userStamp);
            startChallenge.updateStamp(stamp);
        }

        startChallenge.updateStatus();

        return StartChallengeConverter.toStartChallengeInfoDTO(startChallenge);
    }

}
