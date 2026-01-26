package org.kwakmunsu.haruhana.domain.storage.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "파일 업로드 완료 요청 DTO")
public record StorageUploadCompleteRequest(
        @Schema(description = "업로드된 파일의 S3 객체 키", example = "uploads/profile-images/uuid-profile-picture.png")
        @NotBlank(message = "객체 키는 비어 있을 수 없습니다.")
        String objectKey
) {

}