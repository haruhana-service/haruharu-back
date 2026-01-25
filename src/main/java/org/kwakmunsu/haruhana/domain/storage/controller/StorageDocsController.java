package org.kwakmunsu.haruhana.domain.storage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import org.kwakmunsu.haruhana.domain.storage.controller.dto.PresignedCreateRequest;
import org.kwakmunsu.haruhana.global.support.error.ErrorType;
import org.kwakmunsu.haruhana.global.support.response.ApiResponse;
import org.kwakmunsu.haruhana.global.swagger.ApiExceptions;
import org.kwakmunsu.haruhana.infrastructure.s3.dto.PresignedUrlResponse;
import org.springframework.http.ResponseEntity;

@Schema( title = "Storage Docs", description = "저장소 관련 API 문서")
public abstract class StorageDocsController {

    @Operation(
            summary = "Presigned URL 생성 - JWT [O]",
            description = """
                    ### 파일 업로드를 위한 Presigned URL을 생성합니다.
                    - 업로드할 파일의 이름과 업로드 유형을 요청합니다.
                    - 성공적으로 생성되면, Presigned URL과 S3 키 정보를 반환합니다.
                    - Presigned URL의 제한 시간은 3분 입니다.
                    """
    )
    @ApiExceptions(values = {
            ErrorType.AWS_S3_ERROR,
            ErrorType.INVALID_FILE_EXTENSION,
            ErrorType.DEFAULT_ERROR
    })
    public abstract ResponseEntity<ApiResponse<PresignedUrlResponse>> createPresignedUrl(PresignedCreateRequest request);

}