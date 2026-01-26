package org.kwakmunsu.haruhana.domain.storage.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.kwakmunsu.haruhana.domain.storage.enums.UploadStatus;
import org.kwakmunsu.haruhana.domain.storage.enums.UploadType;
import org.kwakmunsu.haruhana.global.entity.BaseEntity;

/**
 * 서버가 발급한 업로드 권한에 대한 기록
 **/
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Storage extends BaseEntity {

    @Column(nullable = false)
    private Long memberId; // 업로드 권한을 발급한 회원 ID

    private Long targetId; // 업로드 대상 ID (예: 회원 프로필 이미지인 경우 회원 ID)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UploadType uploadType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UploadStatus uploadStatus;

    @Column(nullable = false, length = 512, unique = true)
    private String objectKey;

    public static Storage issue(
            Long memberId,
            UploadType uploadType,
            String objectKey
    ) {
        Storage storage = new Storage();

        storage.memberId = memberId;
        storage.targetId = uploadType == UploadType.PROFILE_IMAGE ? memberId : null;
        storage.uploadType = uploadType;
        storage.objectKey = objectKey;
        storage.uploadStatus = UploadStatus.ISSUED;

        return storage;
    }

    public void complete(Long targetId) {
        if (this.uploadStatus == UploadStatus.COMPLETED) {
            return;
        }

        this.targetId = targetId;
        this.uploadStatus = UploadStatus.COMPLETED;
    }

    public boolean isComplete() {
        return this.uploadStatus == UploadStatus.COMPLETED;
    }

}