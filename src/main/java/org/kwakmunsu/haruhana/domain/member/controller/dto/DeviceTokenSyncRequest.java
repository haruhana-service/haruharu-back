package org.kwakmunsu.haruhana.domain.member.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "디바이스 토큰 동기화 요청")
public record DeviceTokenSyncRequest(
        @Schema(description = "디바이스 토큰", example = "fcm_device_token_12345")
        String deviceToken
) {

}