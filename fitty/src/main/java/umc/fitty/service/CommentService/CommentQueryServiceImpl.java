package umc.fitty.service.CommentService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.fitty.apiPayload.code.ErrorCode;
import umc.fitty.apiPayload.exception.CustomException;
import umc.fitty.domain.Comment;
import umc.fitty.domain.RunRecord;
import umc.fitty.repository.CommentRepository;
import umc.fitty.repository.RecordRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentQueryServiceImpl implements CommentQueryService{

    private final RecordRepository recordRepository;
    private final CommentRepository commentRepository;


    @Override
    public Page<Comment> getCommentList(Long recordId, int page) {
        RunRecord runRecord = recordRepository.findById(recordId)
                .orElseThrow(() -> new CustomException(ErrorCode.RECORD_NOT_FOUND));

        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

        return commentRepository.findByRecord(runRecord, pageable);
    }
}
