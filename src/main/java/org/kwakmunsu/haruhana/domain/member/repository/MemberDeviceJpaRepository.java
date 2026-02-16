package org.kwakmunsu.haruhana.domain.member.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import org.kwakmunsu.haruhana.domain.member.entity.MemberDevice;
import org.kwakmunsu.haruhana.global.entity.EntityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberDeviceJpaRepository extends JpaRepository<MemberDevice, Long> {

    Optional<MemberDevice> findByMemberIdAndDeviceToken(Long memberId, String deviceToken);
    Optional<MemberDevice> findByMemberIdAndStatus(Long memberId, EntityStatus status);
    void deleteByMemberIdAndDeviceToken(Long memberId, String deviceToken);
    boolean existsByMemberIdAndDeviceToken(Long memberId, String deviceToken);

    @Modifying
    @Query(value = """
            DELETE FROM member_device
            WHERE id IN (
                SELECT id FROM (
                    SELECT id FROM member_device
                    WHERE last_synced_at <= :cutoffDateTime
                    ORDER BY id
                    LIMIT :batchSize
                ) AS temp
            )
            """, nativeQuery = true
    )
    int deleteExpiredTokensBatch(
            @Param("cutoffDateTime") LocalDateTime cutoffDateTime,
            @Param("batchSize") int batchSize
    );

}