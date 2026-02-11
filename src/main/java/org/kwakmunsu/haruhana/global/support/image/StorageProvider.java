package org.kwakmunsu.haruhana.global.support.image;

import org.kwakmunsu.haruhana.domain.storage.enums.FileContentType;
import org.kwakmunsu.haruhana.domain.storage.enums.UploadType;
import org.kwakmunsu.haruhana.infrastructure.s3.dto.PresignedUrlResponse;

public interface StorageProvider {

    /**
     * 업로드용 Presigned URL 생성
     * @param uploadType - 업로드 타입
     * @param fileContentType - 파일 컨텐츠 타입
     *
     * @return PresignedUrlResponse - 업로드용 Presigned URL 및 S3 키 정보
    * */
    PresignedUrlResponse generatePresignedUploadUrl(UploadType uploadType, FileContentType fileContentType);

    String generatePresignedReadUrl(String objectKey);

    /**
     * S3 객체 존재 여부 확인
     * @param objectKey - S3 객체 키
     *
     * @throws RuntimeException - 객체가 존재하지 않을 경우 예외 발생
     * */
    void ensureObjectExists(String objectKey);

    /**
     * S3 객체 삭제
     * @param oldKey - 삭제할 S3 객체 키
     * */
    void deleteObjectAsync(String oldKey);
}