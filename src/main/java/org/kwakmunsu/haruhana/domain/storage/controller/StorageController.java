package org.kwakmunsu.haruhana.domain.storage.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.kwakmunsu.haruhana.domain.storage.controller.dto.PresignedCreateRequest;
import org.kwakmunsu.haruhana.domain.storage.controller.dto.StorageUploadCompleteRequest;
import org.kwakmunsu.haruhana.domain.storage.service.StorageService;
import org.kwakmunsu.haruhana.global.annotation.LoginMember;
import org.kwakmunsu.haruhana.global.support.response.ApiResponse;
import org.kwakmunsu.haruhana.infrastructure.s3.dto.PresignedUrlResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class StorageController extends StorageDocsController {

    private final StorageService storageService;

    @Override
    @PostMapping("/v1/storage/presigned-url")
    public ResponseEntity<ApiResponse<PresignedUrlResponse>> createPresignedUrl(
            @RequestBody @Valid PresignedCreateRequest request,
            @LoginMember Long memberId
    ) {
        PresignedUrlResponse response = storageService.createPresignedUrl(request.fileName(), request.uploadType(), memberId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Override
    @PostMapping("/v1/storage/upload-complete")
    public ResponseEntity<ApiResponse<?>> uploadComplete(
            @RequestBody @Valid StorageUploadCompleteRequest request,
            @LoginMember Long memberId
    ) {
        storageService.completeUpload(request.objectKey(), memberId);

        return ResponseEntity.ok(ApiResponse.success());
    }

}