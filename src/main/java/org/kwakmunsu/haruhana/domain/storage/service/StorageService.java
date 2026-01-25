package org.kwakmunsu.haruhana.domain.storage.service;

import lombok.RequiredArgsConstructor;
import org.kwakmunsu.haruhana.domain.storage.enums.FileContentType;
import org.kwakmunsu.haruhana.domain.storage.enums.UploadType;
import org.kwakmunsu.haruhana.global.support.image.StorageProvider;
import org.kwakmunsu.haruhana.infrastructure.s3.dto.PresignedUrlResponse;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class StorageService {

    private final StorageProvider storageProvider;
    private final FileValidator fileValidator;

    public PresignedUrlResponse createPresignedUrl(String fileName, UploadType uploadType) {
        fileValidator.validateFile(fileName);

        return storageProvider.generatePresignedUploadUrl(uploadType, FileContentType.fromFileName(fileName));
    }

}