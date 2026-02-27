package org.kwakmunsu.haruhana.admin.member.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "회원 닉네임 변경 요청 DTO")
public record MemberUpdateNicknameRequest(
        @Schema(description = "변경할 닉네임", example = "새닉네임")
        @NotBlank(message = "닉네임은 필수입니다.")
        @Size(max = 50, message = "닉네임은 최대 50자까지 가능합니다.")
        String nickname
) {

}