package com.capstone.fertility.domain.community.service.query;

import com.capstone.fertility.domain.community.dto.res.CommunityResDTO;
import com.capstone.fertility.domain.community.entity.Comment;
import com.capstone.fertility.domain.community.enums.CommentStatus;
import com.capstone.fertility.domain.community.repository.CommunityCommentLikeRepository;
import com.capstone.fertility.domain.community.repository.CommunityCommentRepository;
import com.capstone.fertility.domain.community.support.CommunityAccessSupport;
import com.capstone.fertility.domain.community.support.CommunityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunityCommentQueryServiceImpl implements CommunityCommentQueryService {

    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 50;

    private final CommunityCommentRepository commentRepository;
    private final CommunityCommentLikeRepository commentLikeRepository;
    private final CommunityAccessSupport accessSupport;
    private final CommunityMapper mapper;

    @Override
    public CommunityResDTO.CommentPage listComments(Long viewerUserId, Long postId, int page, int size) {
        accessSupport.requireActivePost(postId);
        int pageSize = size <= 0 ? DEFAULT_SIZE : Math.min(size, MAX_SIZE);
        int pageIndex = Math.max(page, 0);

        List<Comment> roots = commentRepository.findByPost_IdAndParentIsNullAndStatusOrderByCreatedAtAsc(
                postId,
                CommentStatus.ACTIVE
        );

        List<Long> allCommentIds = new ArrayList<>();
        for (Comment root : roots) {
            allCommentIds.add(root.getId());
            commentRepository.findByParent_IdAndStatusOrderByCreatedAtAsc(root.getId(), CommentStatus.ACTIVE)
                    .forEach(reply -> allCommentIds.add(reply.getId()));
        }
        Set<Long> likedIds = allCommentIds.isEmpty()
                ? Set.of()
                : commentLikeRepository.findLikedCommentIds(viewerUserId, allCommentIds);

        List<CommunityResDTO.CommentItem> items = roots.stream()
                .map(root -> {
                    List<Comment> replies = commentRepository.findByParent_IdAndStatusOrderByCreatedAtAsc(
                            root.getId(),
                            CommentStatus.ACTIVE
                    );
                    List<CommunityResDTO.CommentItem> replyItems = replies.stream()
                            .map(reply -> mapper.toComment(reply, List.of(), likedIds.contains(reply.getId()), viewerUserId))
                            .toList();
                    return mapper.toComment(root, replyItems, likedIds.contains(root.getId()), viewerUserId);
                })
                .toList();

        int from = Math.min(pageIndex * pageSize, items.size());
        int to = Math.min(from + pageSize, items.size());
        List<CommunityResDTO.CommentItem> pageContent = items.subList(from, to);
        int totalPages = pageSize == 0 ? 0 : (int) Math.ceil((double) items.size() / pageSize);

        return CommunityResDTO.CommentPage.builder()
                .content(pageContent)
                .page(pageIndex)
                .size(pageSize)
                .totalElements(items.size())
                .totalPages(totalPages)
                .hasNext(to < items.size())
                .build();
    }
}
