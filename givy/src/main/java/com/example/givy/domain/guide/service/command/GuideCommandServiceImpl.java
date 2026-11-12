package com.example.givy.domain.guide.service.command;

import com.example.givy.domain.guide.code.GuideErrorCode;
import com.example.givy.domain.guide.entity.Guide;
import com.example.givy.domain.guide.entity.GuideLike;
import com.example.givy.domain.guide.entity.GuideStore;
import com.example.givy.domain.guide.exception.GuideException;
import com.example.givy.domain.guide.repository.GuideLikeRepository;
import com.example.givy.domain.guide.repository.GuideRepository;
import com.example.givy.domain.guide.repository.GuideStoreRespository;
import com.example.givy.domain.user.code.UserErrorCode;
import com.example.givy.domain.user.entity.Users;
import com.example.givy.domain.user.exception.UserException;
import com.example.givy.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class GuideCommandServiceImpl implements GuideCommandService {

    private final GuideRepository guideRepository;
    private final GuideLikeRepository guideLikeRepository;
    private final GuideStoreRespository guideStoreRespository;
    private final UserRepository userRepository;


    /* 04-03 가이드 단건 좋아요 API */
    @Override
    public void toggleLike(Long guideId, Long userId) {
        //Optional은 값이 있을 수도 있고 없을 수도 있음을 나타내는 것.
        //orElseThorw는 존재해야만 하는 데이터에 쓰는 거고, orElse는 없어도 정상 흐름인 경우임.
        Guide guide = guideRepository.findById(guideId)
                .orElseThrow(() -> new GuideException(GuideErrorCode.GUIDE_NOT_FOUND));
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_ID_NOT_FOUND));

        GuideLike like = guideLikeRepository.findByGuideAndUsers(guide, user).orElse(null);

        //한 번도 좋아요 안누른 경우
        if(like == null){
            guideLikeRepository.save(
                    GuideLike.builder()
                            .guide(guide)
                            .users(user)
                            .build()
            );
            guide.increaseLikeCount();
            return;
        }

        //좋아요 이미 있었다면
        if(like.getIsDeleted() == true){
            like.restore();
            guide.increaseLikeCount();
        }else{
            like.delete();
            guide.decreaseLikeCount();
        }
    }

    /* 04-04 가이드 단건 저장 API */
    @Override
    public void toggleStore(Long guideId, Long userId) {


        Guide guide = guideRepository.findById(guideId)
                .orElseThrow(() -> new GuideException(GuideErrorCode.GUIDE_NOT_FOUND));

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_ID_NOT_FOUND));

        GuideStore store = guideStoreRespository
                .findByGuideAndUsers(guide, user)
                .orElse(null);

        if (store == null) {
            guideStoreRespository.save(
                    GuideStore.builder()
                            .guide(guide)
                            .users(user)
                            .build()
            );
            guide.increaseStoreCount();
            return;
        }

        // 이미 저장 기록이 있는 경우
        if (store.getIsDeleted() == true) {
            // 다시 저장
            store.restore();
            guide.increaseStoreCount();
        } else {
            // 저장 취소
            store.delete();
            guide.decreaseStoreCount();
        }
    }


}
