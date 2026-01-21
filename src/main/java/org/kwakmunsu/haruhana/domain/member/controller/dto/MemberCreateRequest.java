package org.kwakmunsu.haruhana.domain.member.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.kwakmunsu.haruhana.domain.member.service.dto.request.NewPreference;
import org.kwakmunsu.haruhana.domain.member.service.dto.request.NewProfile;
import org.kwakmunsu.haruhana.domain.problem.enums.ProblemDifficulty;

@Schema(description = "회원 생성 요청 DTO")
@Builder
public record MemberCreateRequest(
        @Schema(description = "로그인 아이디", example = "user123")
        @Size(max = 50, message = "로그인 아이디는 최대 50자까지 가능합니다.")
        @NotBlank(message = "로그인 아이디는 필수입니다.")
        String loginId,

        @Schema(description = "비밀번호 (영문 대소문자, 숫자 포함)", example = "Password123!")
        @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
        @NotBlank(message = "비밀번호는 필수입니다.")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
                message = "비밀번호는 영문 소문자, 대문자, 숫자를 모두 포함해야 합니다."
        )
        String password,

        @Schema(description = "닉네임", example = "HappyUser")
        @Size(max = 50, message = "닉네임은 최대 50자까지 가능합니다.")
        @NotBlank(message = "닉네임은 필수입니다.")
        String nickname,

        @Schema(description = "카테고리 ID", example = "1")
        @NotNull(message = "카테고리는 필수입니다.")
        Long categoryTopicId,

        @Schema(description = "문제 난이도", example = "EASY")
        @NotNull(message = "난이도는 필수입니다.")
        ProblemDifficulty difficulty
) {

    public NewProfile toNewProfile() {
        return NewProfile.builder()
                .loginId(loginId)
                .password(password)
                .nickname(nickname)
                .build();
    }

    public NewPreference toNewPreference() {
        return NewPreference.builder()
                .categoryTopicId(categoryTopicId)
                .difficulty(difficulty)
                .build();
    }

}