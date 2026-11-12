package com.capstone.fertility.domain.user.dto.req;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserReqDTO {

    /**
     * 회원가입을 위한 요청 DTO.
     * <p>isTermsAgreed 는 필수이며 반드시 true 여야 한다. (약관 미동의 가입 차단)</p>
     */
    public record SignUpReqDTO(
            @NotBlank(message = "이메일은 필수 입력 값입니다.")
            @Email(message = "이메일 형식이 올바르지 않습니다.")
            String email,

            @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
            @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
            String password,

            @NotBlank(message = "닉네임은 필수 입력 값입니다.")
            @Size(min = 2, max = 10, message = "닉네임은 2자 이상 10자 이하로 입력해주세요.")
            String nickname,

            @NotNull(message = "약관 동의 여부(isTermsAgreed)는 필수입니다.")
            @AssertTrue(message = "약관에 동의해야 회원가입할 수 있습니다.")
            Boolean isTermsAgreed
    ) {}

    /**
     * 프로필 수정(닉네임, 프로필 이미지)을 위한 요청 DTO
     * PATCH 요청이므로, 클라이언트가 보내지 않는 필드는 null이 될 수 있어야 합니다.
     */
    /**
     * PATCH /users/me — 보낸 필드만 반영.
     * gender: M | F (MALE/FEMALE 허용)
     */
    @Getter
    @NoArgsConstructor
    public static class UpdateProfileDTO {
        private String nickname;
        private String profileImageUrl;
        /** 표시 이름 1~20자(trim 후 저장). nickname(계정명)과 분리. */
        private String displayName;
        /** 프로필 기본 성별 — 다음 검사 시작 시 기본값으로 활용 가능 */
        private String gender;
    }

    public record LoginReqDTO(
            @NotBlank(message = "이메일을 입력해주세요.")
            @Email(message = "이메일 형식이 올바르지 않습니다.")
            String email,

            @NotBlank(message = "비밀번호를 입력해주세요.")
            String password
    ) {}
}