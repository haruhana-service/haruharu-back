package org.kwakmunsu.haruhana.domain.streak.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import org.kwakmunsu.haruhana.domain.streak.entity.Streak;
import org.kwakmunsu.haruhana.global.entity.EntityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StreakJpaRepository extends JpaRepository<Streak, Long> {

    Optional<Streak> findByMemberIdAndStatus(Long memberId, EntityStatus status);
    boolean existsByMemberIdAndStatus(Long memberId, EntityStatus entityStatus);

    @Modifying
    @Query("UPDATE Streak s SET s.status = :status, s.updatedAt = :now WHERE s.member.id = :memberId")
    void softDeleteByMemberId(@Param("memberId") Long memberId, @Param("status") EntityStatus status, @Param("now") LocalDateTime now);
}