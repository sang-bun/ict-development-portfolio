package com.capstone.fertility.domain.community.service.command;

import com.capstone.fertility.domain.community.dto.req.CommunityReqDTO;
import com.capstone.fertility.domain.community.entity.CommunityUserBlock;
import com.capstone.fertility.domain.community.entity.Post;
import com.capstone.fertility.domain.community.entity.PostReport;
import com.capstone.fertility.domain.community.exception.CommunityException;
import com.capstone.fertility.domain.community.exception.code.CommunityErrorCode;
import com.capstone.fertility.domain.community.repository.CommunityPostReportRepository;
import com.capstone.fertility.domain.community.repository.CommunityUserBlockRepository;
import com.capstone.fertility.domain.community.support.CommunityAccessSupport;
import com.capstone.fertility.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommunityModerationCommandServiceImpl implements CommunityModerationCommandService {

    private final CommunityPostReportRepository postReportRepository;
    private final CommunityUserBlockRepository userBlockRepository;
    private final CommunityAccessSupport accessSupport;

    @Override
    public void reportPost(Long reporterId, Long postId, CommunityReqDTO.PostReport request) {
        Post post = accessSupport.requireActivePost(postId);
        User reporter = accessSupport.requireUser(reporterId);
        if (postReportRepository.existsByPost_IdAndReporter_Id(postId, reporterId)) {
            throw new CommunityException(CommunityErrorCode.ALREADY_REPORTED);
        }
        if (request.getReason() == null) {
            throw new CommunityException(CommunityErrorCode.INVALID_REQUEST);
        }
        postReportRepository.save(PostReport.builder()
                .post(post)
                .reporter(reporter)
                .reason(request.getReason())
                .detail(request.getDetail())
                .build());
    }

    @Override
    public void blockUser(Long blockerId, Long blockedUserId) {
        if (blockerId.equals(blockedUserId)) {
            throw new CommunityException(CommunityErrorCode.CANNOT_BLOCK_SELF);
        }
        User blocker = accessSupport.requireUser(blockerId);
        User blocked = accessSupport.requireUser(blockedUserId);
        if (userBlockRepository.existsByBlocker_IdAndBlocked_Id(blockerId, blockedUserId)) {
            return;
        }
        userBlockRepository.save(CommunityUserBlock.builder()
                .blocker(blocker)
                .blocked(blocked)
                .build());
    }
}
