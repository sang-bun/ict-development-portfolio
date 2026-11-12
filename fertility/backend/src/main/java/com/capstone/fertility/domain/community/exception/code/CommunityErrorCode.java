package com.capstone.fertility.domain.community.exception.code;

import com.capstone.fertility.global.apiPayLoad.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CommunityErrorCode implements BaseErrorCode {

    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMUNITY404_1", "게시글을 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMUNITY404_2", "댓글을 찾을 수 없습니다."),
    POST_FORBIDDEN(HttpStatus.FORBIDDEN, "COMMUNITY403_1", "본인의 게시글만 수정·삭제할 수 있습니다."),
    COMMENT_FORBIDDEN(HttpStatus.FORBIDDEN, "COMMUNITY403_2", "본인의 댓글만 수정·삭제할 수 있습니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "COMMUNITY400_1", "요청 데이터가 유효하지 않습니다."),
    INVALID_CATEGORY(HttpStatus.BAD_REQUEST, "COMMUNITY400_2", "유효하지 않은 카테고리입니다."),
    INVALID_PARENT_COMMENT(HttpStatus.BAD_REQUEST, "COMMUNITY400_3", "유효하지 않은 부모 댓글입니다."),
    IMAGE_UPLOAD_FAILED(HttpStatus.BAD_REQUEST, "COMMUNITY400_4", "이미지 업로드에 실패했습니다."),
    IMAGE_TOO_LARGE(HttpStatus.BAD_REQUEST, "COMMUNITY400_5", "이미지 크기는 5MB 이하여야 합니다."),
    IMAGE_TYPE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "COMMUNITY400_6", "지원하지 않는 이미지 형식입니다."),
    ALREADY_REPORTED(HttpStatus.CONFLICT, "COMMUNITY409_1", "이미 신고한 게시글입니다."),
    CANNOT_BLOCK_SELF(HttpStatus.BAD_REQUEST, "COMMUNITY400_7", "자기 자신은 차단할 수 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMUNITY404_3", "사용자를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
