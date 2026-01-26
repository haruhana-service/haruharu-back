package org.kwakmunsu.haruhana.domain.storage.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "파일 업로드 완료 요청 DTO")
public record StorageUploadCompleteRequest(
        @Schema(description = "업로드된 파일의 S3 객체 키", example = "uploads/profile-images/uuid-profile-picture.png")
        @Size(max = 512, message = "객체 키는 최대 {max}자를 초과할 수 없습니다.")
        @NotBlank(message = "객체 키는 비어 있을 수 없습니다.")
        String objectKey
) {

}