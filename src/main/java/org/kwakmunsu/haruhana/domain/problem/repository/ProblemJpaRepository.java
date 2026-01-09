package org.kwakmunsu.haruhana.domain.problem.repository;

import org.kwakmunsu.haruhana.domain.problem.entity.Problem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProblemJpaRepository extends JpaRepository<Problem, Long> {

}