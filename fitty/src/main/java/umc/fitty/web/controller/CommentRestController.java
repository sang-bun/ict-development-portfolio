package umc.fitty.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import umc.fitty.apiPayload.ApiResponse;
import umc.fitty.converter.CommentConverter;
import umc.fitty.domain.Comment;
import umc.fitty.service.CommentService.CommentCommandService;
import umc.fitty.service.CommentService.CommentQueryService;
import umc.fitty.web.dto.CommentDTO.CommentRequestDTO;
import umc.fitty.web.dto.CommentDTO.CommentResponseDTO;

@RestController
@RequiredArgsConstructor
@RequestMapping("/feed")
public class CommentRestController {

    private final CommentCommandService commentCommandService;
    private final CommentQueryService commentQueryService;

    @PostMapping("/{recordId}/comments")
    public ApiResponse<CommentResponseDTO.CreateCommentResultDTO> createComment(
            @PathVariable(name = "recordId") Long recordId, @Valid @RequestBody CommentRequestDTO.CreateCommentDTO request) {


        Comment newComment = commentCommandService.createComment(recordId, request);

        return ApiResponse.success("댓글이 성공적으로 작성되었습니다.", CommentConverter.toCreateCommentResultDTO(newComment));
    }

    @GetMapping("/{recordId}/comments")
    public ApiResponse<CommentResponseDTO.CommentListDTO> getCommentList(
            @PathVariable(name = "recordId") Long recordId,
            @RequestParam(name = "page") Integer page){

        Page<Comment> commentPage = commentQueryService.getCommentList(recordId, page);

        return ApiResponse.success("댓글 목록 조회가 성공했습니다.", CommentConverter.toCommentListDTO(commentPage));
    }
}
