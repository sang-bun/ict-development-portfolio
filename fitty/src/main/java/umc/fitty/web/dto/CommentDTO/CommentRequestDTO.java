package umc.fitty.web.dto.CommentDTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

public class CommentRequestDTO {

    @Getter
    public static class CreateCommentDTO {

        @NotBlank(message = "댓글 내용은 필수 입력값입니다.")
        private String content;
    }
}
