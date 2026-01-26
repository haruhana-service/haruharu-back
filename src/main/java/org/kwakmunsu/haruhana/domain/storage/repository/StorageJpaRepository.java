package org.kwakmunsu.haruhana.domain.storage.repository;

import java.util.Optional;
import org.kwakmunsu.haruhana.domain.storage.entity.Storage;
import org.kwakmunsu.haruhana.global.entity.EntityStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StorageJpaRepository extends JpaRepository<Storage, Long> {

    boolean existsByMemberIdAndObjectKeyAndStatus(Long memberId, String objectKey, EntityStatus status);
    Optional<Storage> findByMemberIdAndObjectKeyAndStatus(
            Long memberId,
            String objectKey,
            EntityStatus status
    );

}