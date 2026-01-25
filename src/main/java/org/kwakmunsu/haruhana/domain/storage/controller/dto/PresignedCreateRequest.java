package org.kwakmunsu.haruhana.domain.storage.controller.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.kwakmunsu.haruhana.domain.storage.enums.UploadType;

@Schema(description = "파일 업로드를 위한 presigned url 요청 DTO")
public record PresignedCreateRequest(
        @Schema(description = "파일 이름", example = "profile-picture.png")
        @NotBlank(message = "파일 이름은 비어 있을 수 없습니다.")
        String fileName,

        @Schema(description = "업로드 유형", example = "PROFILE_IMAGE")
        @NotNull(message = "업로드 유형은 비어 있을 수 없습니다.")
        UploadType uploadType
) {

}