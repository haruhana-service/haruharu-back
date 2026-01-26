package org.kwakmunsu.haruhana.infrastructure.s3.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "Presigned URL 응답 DTO")
@Builder
public record PresignedUrlResponse(
        @Schema(description = "Presigned URL (만료 기간 - 3분)", example = "https://example-bucket.s3.amazonaws.com/your-file?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=...")
        String presignedUrl,

        @Schema(description = "S3 객체 키", example = "uploads/2024/06/your-file.jpg")
        String objectKey
) {

}