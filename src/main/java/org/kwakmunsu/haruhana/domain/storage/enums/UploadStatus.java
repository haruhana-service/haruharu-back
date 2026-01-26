package org.kwakmunsu.haruhana.domain.storage.enums;

public enum UploadStatus {

    ISSUED,    // 업로드를 위한 presigned URL 발급됨
    COMPLETED, // 실제 업로드 + 완료 처리됨
    EXPIRED    // presigned Url 유효기간 만료

}