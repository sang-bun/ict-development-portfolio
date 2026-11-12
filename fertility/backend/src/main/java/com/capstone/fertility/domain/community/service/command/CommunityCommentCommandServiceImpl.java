package com.capstone.fertility.domain.community.service.command;

import com.capstone.fertility.domain.community.dto.req.CommunityReqDTO;
import com.capstone.fertility.domain.community.dto.res.CommunityResDTO;
import com.capstone.fertility.domain.community.entity.Comment;
import com.capstone.fertility.domain.community.entity.CommentLike;
import com.capstone.fertility.domain.community.entity.Post;
import com.capstone.fertility.domain.community.enums.CommentStatus;
import com.capstone.fertility.domain.community.exception.CommunityException;
import com.capstone.fertility.domain.community.exception.code.CommunityErrorCode;
import com.capstone.fertility.domain.community.repository.CommunityCommentLikeRepository;
import com.capstone.fertility.domain.community.repository.CommunityCommentRepository;
import com.capstone.fertility.domain.community.support.CommunityAccessSupport;
import com.capstone.fertility.domain.community.support.CommunityMapper;
import com.capstone.fertility.domain.community.support.CommunityValidationSupport;
import com.capstone.fertility.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommunityCommentCommandServiceImpl implements CommunityCommentCommandService {

    private final CommunityCommentRepository commentRepository;
    private final CommunityCommentLikeRepository commentLikeRepository;
    private final CommunityAccessSupport accessSupport;
    private final CommunityMapper mapper;

    @Override
    public CommunityResDTO.CommentItem createComment(Long userId, Long postId, CommunityReqDTO.CommentWrite request) {
        Post post = accessSupport.requireActivePost(postId);
        User author = accessSupport.requireUser(userId);
        String body = CommunityValidationSupport.requireCommentBody(request.getBody());

        Comment parent = null;
        if (request.getParentCommentId() != null) {
            parent = commentRepository.findByIdWithAuthor(request.getParentCommentId())
                    .orElseThrow(() -> new CommunityException(CommunityErrorCode.INVALID_PARENT_COMMENT));
            if (!parent.isActive() || !parent.getPost().getId().equals(postId)) {
                throw new CommunityException(CommunityErrorCode.INVALID_PARENT_COMMENT);
            }
            if (parent.getParent() != null) {
                parent = parent.getParent();
            }
        }

        Comment comment = Comment.builder()
                .post(post)
                .author(author)
                .parent(parent)
                .body(body)
                .build();
        commentRepository.save(comment);
        post.adjustCommentCount(1);
        return mapper.toComment(comment, List.of(), false, userId);
    }

    @Override
    public CommunityResDTO.CommentItem updateComment(Long userId, Long commentId, CommunityReqDTO.CommentWrite request) {
        Comment comment = requireOwnedActiveComment(commentId, userId);
        comment.updateBody(CommunityValidationSupport.requireCommentBody(request.getBody()));
        return mapper.toComment(comment, List.of(), commentLikeRepository.existsByComment_IdAndUser_Id(commentId, userId), userId);
    }

    @Override
    public void deleteComment(Long userId, Long commentId) {
        Comment comment = requireOwnedActiveComment(commentId, userId);
        comment.softDelete();
        comment.getPost().adjustCommentCount(-1);
    }

    @Override
    public CommunityResDTO.LikeToggle toggleCommentLike(Long userId, Long commentId) {
        Comment comment = commentRepository.findByIdWithAuthor(commentId)
                .filter(Comment::isActive)
                .orElseThrow(() -> new CommunityException(CommunityErrorCode.COMMENT_NOT_FOUND));
        User user = accessSupport.requireUser(userId);
        boolean liked;
        var existing = commentLikeRepository.findByComment_IdAndUser_Id(commentId, userId);
        if (existing.isPresent()) {
            commentLikeRepository.delete(existing.get());
            comment.adjustLikeCount(-1);
            liked = false;
        } else {
            commentLikeRepository.save(CommentLike.builder().comment(comment).user(user).build());
            comment.adjustLikeCount(1);
            liked = true;
        }
        return CommunityResDTO.LikeToggle.builder()
                .liked(liked)
                .likeCount(comment.getLikeCount())
                .build();
    }

    private Comment requireOwnedActiveComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findByIdWithAuthor(commentId)
                .orElseThrow(() -> new CommunityException(CommunityErrorCode.COMMENT_NOT_FOUND));
        if (!comment.isActive()) {
            throw new CommunityException(CommunityErrorCode.COMMENT_NOT_FOUND);
        }
        if (!comment.isAuthor(userId)) {
            throw new CommunityException(CommunityErrorCode.COMMENT_FORBIDDEN);
        }
        return comment;
    }
}
