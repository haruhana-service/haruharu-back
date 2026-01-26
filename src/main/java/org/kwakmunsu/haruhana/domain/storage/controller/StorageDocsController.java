package org.kwakmunsu.haruhana.domain.storage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import org.kwakmunsu.haruhana.domain.storage.controller.dto.PresignedCreateRequest;
import org.kwakmunsu.haruhana.domain.storage.controller.dto.StorageUploadCompleteRequest;
import org.kwakmunsu.haruhana.global.support.error.ErrorType;
import org.kwakmunsu.haruhana.global.support.response.ApiResponse;
import org.kwakmunsu.haruhana.global.swagger.ApiExceptions;
import org.kwakmunsu.haruhana.infrastructure.s3.dto.PresignedUrlResponse;
import org.springframework.http.ResponseEntity;

@Schema(title = "Storage Docs", description = "저장소 관련 API 문서")
public abstract class StorageDocsController {

    @Operation(
            summary = "Presigned URL 생성 - JWT [O]",
            description = """
                    ### 파일 업로드를 위한 Presigned URL을 생성합니다.
                    - 업로드할 파일의 이름과 업로드 유형을 요청합니다.
                    - 성공적으로 생성되면, Presigned URL과 S3 Object Key를 반환합니다.
                    - Presigned URL의 제한 시간은 3분 입니다.
                    """
    )
    @ApiExceptions(values = {
            ErrorType.AWS_S3_ERROR,
            ErrorType.INVALID_FILE_EXTENSION,
            ErrorType.DEFAULT_ERROR
    })
    public abstract ResponseEntity<ApiResponse<PresignedUrlResponse>> createPresignedUrl(
            PresignedCreateRequest request,
            Long memberId
    );

    @Operation(
            summary = "업로드 완료 처리 - JWT [O]",
            description = """
                    ### 파일 업로드 완료를 처리합니다.
                    - 클라이언트가 파일 업로드를 완료한 후, 해당 완료 요청을 보냅니다.
                    - 요청에는 업로드된 파일의 S3 Object Key가 포함됩니다.
                    - 서버는 해당 Object Key를 검증하고, 업로드 상태를 완료로 변경합니다.
                    - 업로드가 성공적으로 완료되면, 빈 응답을 반환합니다.
                    """
    )
    @ApiExceptions(values = {
            ErrorType.AWS_S3_ERROR,
            ErrorType.STORAGE_ISSUE_NOT_FOUND,
            ErrorType.DEFAULT_ERROR
    })
    public abstract ResponseEntity<ApiResponse<?>> uploadComplete(
            StorageUploadCompleteRequest request,
            Long memberId
    );

}