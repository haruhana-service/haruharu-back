package org.kwakmunsu.haruhana.domain.streak.repository;

import java.util.Optional;
import org.kwakmunsu.haruhana.domain.streak.entity.Streak;
import org.kwakmunsu.haruhana.global.entity.EntityStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StreakJpaRepository extends JpaRepository<Streak, Long> {

    Optional<Streak> findByMemberIdAndStatus(Long memberId, EntityStatus status);
}