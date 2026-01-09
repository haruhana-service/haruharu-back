package org.kwakmunsu.haruhana.domain.dailyproblem.repository;

import org.kwakmunsu.haruhana.domain.dailyproblem.entity.DailyProblem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyProblemJpaRepository extends JpaRepository<DailyProblem, Long> {

}