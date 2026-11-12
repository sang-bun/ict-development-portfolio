package com.capstone.fertility.domain.community.exception.code;

import com.capstone.fertility.global.apiPayLoad.code.BaseSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CommunitySuccessCode implements BaseSuccessCode {

    POST_LIST_FETCHED(HttpStatus.OK, "COMMUNITY200_1", "게시글 목록을 조회했습니다."),
    POST_DETAIL_FETCHED(HttpStatus.OK, "COMMUNITY200_2", "게시글 상세를 조회했습니다."),
    POST_CREATED(HttpStatus.OK, "COMMUNITY200_3", "게시글을 작성했습니다."),
    POST_UPDATED(HttpStatus.OK, "COMMUNITY200_4", "게시글을 수정했습니다."),
    POST_DELETED(HttpStatus.OK, "COMMUNITY200_5", "게시글을 삭제했습니다."),
    POST_LIKE_TOGGLED(HttpStatus.OK, "COMMUNITY200_6", "게시글 좋아요를 반영했습니다."),
    POST_BOOKMARK_TOGGLED(HttpStatus.OK, "COMMUNITY200_7", "게시글 북마크를 반영했습니다."),
    COMMENT_LIST_FETCHED(HttpStatus.OK, "COMMUNITY200_8", "댓글 목록을 조회했습니다."),
    COMMENT_CREATED(HttpStatus.OK, "COMMUNITY200_9", "댓글을 작성했습니다."),
    COMMENT_UPDATED(HttpStatus.OK, "COMMUNITY200_10", "댓글을 수정했습니다."),
    COMMENT_DELETED(HttpStatus.OK, "COMMUNITY200_11", "댓글을 삭제했습니다."),
    COMMENT_LIKE_TOGGLED(HttpStatus.OK, "COMMUNITY200_12", "댓글 좋아요를 반영했습니다."),
    IMAGE_UPLOADED(HttpStatus.OK, "COMMUNITY200_13", "이미지를 업로드했습니다."),
    POST_REPORTED(HttpStatus.OK, "COMMUNITY200_14", "게시글을 신고했습니다."),
    USER_BLOCKED(HttpStatus.OK, "COMMUNITY200_15", "사용자를 차단했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
