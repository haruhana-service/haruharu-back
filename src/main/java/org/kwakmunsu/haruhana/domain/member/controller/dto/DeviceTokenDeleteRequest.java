package org.kwakmunsu.haruhana.domain.member.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "디바이스 토큰 삭제 요청")
public record DeviceTokenDeleteRequest(
        @Schema(description = "디바이스 토큰", example = "fcm_device_token_12345")
        @NotBlank(message = "디바이스 토큰은 필수입니다.")
        String deviceToken
) {

}