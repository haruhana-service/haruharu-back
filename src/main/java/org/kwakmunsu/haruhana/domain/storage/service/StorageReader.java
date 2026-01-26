package org.kwakmunsu.haruhana.domain.storage.service;

import lombok.RequiredArgsConstructor;
import org.kwakmunsu.haruhana.domain.storage.entity.Storage;
import org.kwakmunsu.haruhana.domain.storage.repository.StorageJpaRepository;
import org.kwakmunsu.haruhana.global.entity.EntityStatus;
import org.kwakmunsu.haruhana.global.support.error.ErrorType;
import org.kwakmunsu.haruhana.global.support.error.HaruHanaException;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class StorageReader {

    private final StorageJpaRepository storageJpaRepository;

    public Storage findByMemberIdAndObjectKey(Long memberId, String objectKey) {
        return storageJpaRepository.findByMemberIdAndObjectKeyAndStatus(
                memberId,
                objectKey,
                EntityStatus.ACTIVE
        ).orElseThrow(() -> new HaruHanaException(ErrorType.STORAGE_ISSUE_NOT_FOUND));
    }

}