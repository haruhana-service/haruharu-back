package org.kwakmunsu.haruhana.domain.storage.service;

import lombok.RequiredArgsConstructor;
import org.kwakmunsu.haruhana.domain.member.entity.Member;
import org.kwakmunsu.haruhana.domain.storage.entity.Storage;
import org.kwakmunsu.haruhana.domain.storage.enums.UploadType;
import org.kwakmunsu.haruhana.domain.storage.repository.StorageJpaRepository;
import org.kwakmunsu.haruhana.global.support.image.StorageProvider;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class StorageManager {

    private final StorageProvider storageProvider;
    private final StorageReader storageReader;
    private final StorageJpaRepository storageJpaRepository;

    public void issue(Long memberId, UploadType uploadType, String objectKey) {
        storageJpaRepository.save(Storage.issue(memberId, uploadType, objectKey));
    }

    // NOTE: 현재 로직은 프로필 이미지 업로드에 한정되어 있지만, 추후 확장 될 경우 업로드 타입에 따른 분기 처리가 필요할 수 있음. Storage는 확장성 고려하여 설계됨
    @Transactional
    public void completeUpload(String objectKey, Member member) {
        // 회원이 발급한 objectKey이 아니라면 예외 발생
        Storage storage = storageReader.findByMemberIdAndObjectKey(member.getId(), objectKey);
        String oldKey = member.getProfileImageObjectKey();

        if (storage.isComplete()) return;

        // S3에 객체 존재 여부 확인
        storageProvider.ensureObjectExists(objectKey);

        // 업로드 완료 처리 후 회원 프로필 이미지 정보 업데이트
        storage.complete(member.getId());
        member.updateProfileImageObjectKey(objectKey);

        if (oldKey != null && !member.isEqualsObjectKey(objectKey)) {
            storageProvider.deleteObjectAsync(oldKey);
        }
    }

}