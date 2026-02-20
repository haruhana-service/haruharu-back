package org.kwakmunsu.haruhana.domain.storage.repository;

import java.util.Optional;
import org.kwakmunsu.haruhana.domain.storage.entity.Storage;
import org.kwakmunsu.haruhana.global.entity.EntityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StorageJpaRepository extends JpaRepository<Storage, Long> {

    Optional<Storage> findByMemberIdAndObjectKeyAndStatus(
            Long memberId,
            String objectKey,
            EntityStatus status
    );

    @Modifying
    @Query("UPDATE Storage s SET s.status = :status WHERE s.memberId = :memberId")
    void softDeleteByMemberId(@Param("memberId") Long memberId, @Param("status") EntityStatus status);

}