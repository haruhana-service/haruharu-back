package org.kwakmunsu.haruhana.domain.storage.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.kwakmunsu.haruhana.domain.storage.entity.Storage;
import org.kwakmunsu.haruhana.domain.storage.enums.UploadStatus;
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

    @Query("SELECT s.objectKey FROM Storage s WHERE s.memberId = :memberId AND s.uploadStatus = :uploadStatus")
    List<String> findObjectKeysByMemberId(@Param("memberId") Long memberId, @Param("uploadStatus") UploadStatus uploadStatus);

    @Modifying
    @Query("UPDATE Storage s SET s.status = :status, s.updatedAt = :now WHERE s.memberId = :memberId AND s.status = 'ACTIVE'")
    void softDeleteByMemberId(@Param("memberId") Long memberId, @Param("status") EntityStatus status, @Param("now") LocalDateTime now);

}