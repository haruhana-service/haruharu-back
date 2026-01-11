package org.kwakmunsu.haruhana.domain.auth.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "로그인 요청 DTO")
public record LoginRequest(
        @Schema(description = "loginId", example = "testLoginId")
        @NotBlank(message = "loginId는 필수 입력 값입니다.")
        String loginId,

        @Schema(description = "사용자 비밀번호", example = "testPassword123!")
        @NotBlank(message = "password는 필수 입력 값입니다.")
        String password
) {

}