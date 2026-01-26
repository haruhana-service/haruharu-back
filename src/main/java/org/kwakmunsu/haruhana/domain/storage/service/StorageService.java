package org.kwakmunsu.haruhana.domain.storage.service;

import lombok.RequiredArgsConstructor;
import org.kwakmunsu.haruhana.domain.member.entity.Member;
import org.kwakmunsu.haruhana.domain.member.service.MemberReader;
import org.kwakmunsu.haruhana.domain.storage.enums.FileContentType;
import org.kwakmunsu.haruhana.domain.storage.enums.UploadType;
import org.kwakmunsu.haruhana.global.support.image.StorageProvider;
import org.kwakmunsu.haruhana.infrastructure.s3.dto.PresignedUrlResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class StorageService {

    private final StorageProvider storageProvider;
    private final StorageManager storageManager;
    private final FileValidator fileValidator;
    private final MemberReader memberReader;

    public PresignedUrlResponse createPresignedUrl(String fileName, UploadType uploadType, Long memberId) {
        fileValidator.validateFile(fileName);

        FileContentType fileContentType = FileContentType.fromFileName(fileName);
        PresignedUrlResponse response = storageProvider.generatePresignedUploadUrl(uploadType, fileContentType);

        storageManager.issue(memberId, uploadType, response.objectKey());

        return response;
    }

    @Transactional
    public void completeUpload(String objectKey, Long memberId) {
        Member member = memberReader.find(memberId);

        storageManager.completeUpload(objectKey, member);
    }

}