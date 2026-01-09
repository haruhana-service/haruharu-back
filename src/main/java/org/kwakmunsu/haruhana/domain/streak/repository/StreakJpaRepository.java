package org.kwakmunsu.haruhana.domain.streak.repository;

import org.kwakmunsu.haruhana.domain.streak.entity.Streak;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StreakJpaRepository extends JpaRepository<Streak, Long> {

}