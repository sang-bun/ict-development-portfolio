package umc.fitty.service.CommentService;

import org.springframework.data.domain.Page;
import umc.fitty.domain.Comment;

public interface CommentQueryService{

    Page<Comment> getCommentList(Long recordId, int page);
}
