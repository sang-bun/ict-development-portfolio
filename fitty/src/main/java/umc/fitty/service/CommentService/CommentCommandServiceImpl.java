package umc.fitty.service.CommentService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.fitty.apiPayload.code.ErrorCode;
import umc.fitty.apiPayload.exception.CustomException;
import umc.fitty.converter.CommentConverter;
import umc.fitty.domain.Comment;
import umc.fitty.domain.RunRecord;
import umc.fitty.domain.User;
import umc.fitty.repository.CommentRepository;
import umc.fitty.repository.RecordRepository;
import umc.fitty.repository.UserRepository;
import umc.fitty.web.dto.CommentDTO.CommentRequestDTO;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentCommandServiceImpl implements CommentCommandService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final RecordRepository recordRepository;

    @Override
    public Comment createComment(Long recordId, CommentRequestDTO.CreateCommentDTO request) {
        User user = findCurrentUser();

        RunRecord record = recordRepository.findById(recordId)
                .orElseThrow(() -> new CustomException(ErrorCode.RECORD_NOT_FOUND));

        Comment newComment = CommentConverter.toComment(request);

        newComment.setUserAndRunRecord(user, record);
        return commentRepository.save(newComment);
    }

    private User findCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        return userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

}
