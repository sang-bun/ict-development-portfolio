package umc.fitty.service.CommentService;

import umc.fitty.domain.Comment;
import umc.fitty.web.dto.CommentDTO.CommentRequestDTO;

public interface CommentCommandService {

    Comment createComment(Long recordId, CommentRequestDTO.CreateCommentDTO request);
}
