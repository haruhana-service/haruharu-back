package org.kwakmunsu.haruhana.domain.storage.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.kwakmunsu.haruhana.domain.storage.controller.dto.PresignedCreateRequest;
import org.kwakmunsu.haruhana.domain.storage.service.StorageService;
import org.kwakmunsu.haruhana.global.support.response.ApiResponse;
import org.kwakmunsu.haruhana.infrastructure.s3.dto.PresignedUrlResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class StorageController extends StorageDocsController {

    private final StorageService storageService;

    @Override
    @PostMapping("/v1/storage/presigned-url")
    public ResponseEntity<ApiResponse<PresignedUrlResponse>> createPresignedUrl(
            @RequestParam @Valid PresignedCreateRequest request
    ) {
        PresignedUrlResponse response = storageService.createPresignedUrl(request.fileName(), request.uploadType());

        return ResponseEntity.ok(ApiResponse.success(response));
    }

}
