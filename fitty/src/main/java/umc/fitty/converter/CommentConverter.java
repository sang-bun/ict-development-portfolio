package umc.fitty.converter;

import org.springframework.data.domain.Page;
import umc.fitty.domain.Comment;
import umc.fitty.web.dto.CommentDTO.CommentRequestDTO;
import umc.fitty.web.dto.CommentDTO.CommentResponseDTO;

import java.util.List;
import java.util.stream.Collectors;

public class CommentConverter {

    public static Comment toComment(CommentRequestDTO.CreateCommentDTO request) {
        return Comment.builder()
                .content(request.getContent())
                .build();
    }

    public static CommentResponseDTO.CreateCommentResultDTO toCreateCommentResultDTO(Comment comment) {
        return CommentResponseDTO.CreateCommentResultDTO.builder()
                .commentId(comment.getId())
                .createdAt(comment.getCreatedAt())
                .build();
    }

    public static CommentResponseDTO.CommentListDTO toCommentListDTO(Page<Comment> commentPage) {

        List<Comment> commentEntityList = commentPage.getContent();

        List<CommentResponseDTO.CommentPreviewDTO> commentPreviewDTOList = commentEntityList.stream()
                .map(CommentConverter::toCommentPreviewDTO)
                .collect(Collectors.toList());

        return CommentResponseDTO.CommentListDTO.builder()
                .commentList(commentPreviewDTOList)
                .listSize(commentPreviewDTOList.size())
                .totalPage(commentPage.getTotalPages())
                .totalElements(commentPage.getTotalElements())
                .isFirst(commentPage.isFirst())
                .isLast(commentPage.isLast())
                .build();
    }

    public static CommentResponseDTO.CommentPreviewDTO toCommentPreviewDTO(Comment comment) {
        return CommentResponseDTO.CommentPreviewDTO.builder()
                .commentId(comment.getId())
                .userNickname(comment.getUser().getEffectiveNickname())
                .userProfileUrl(comment.getUser().getEffectiveProfileImageUrl())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
