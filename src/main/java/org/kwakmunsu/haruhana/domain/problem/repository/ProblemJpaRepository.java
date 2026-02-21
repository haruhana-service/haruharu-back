package org.kwakmunsu.haruhana.domain.problem.repository;

import java.util.Optional;
import org.kwakmunsu.haruhana.domain.problem.entity.Problem;
import org.kwakmunsu.haruhana.domain.problem.enums.ProblemDifficulty;
import org.kwakmunsu.haruhana.global.entity.EntityStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProblemJpaRepository extends JpaRepository<Problem, Long> {

    Optional<Problem> findFirstByCategoryTopicIdAndDifficultyAndStatusOrderByProblemAtDesc(
            Long categoryTopicId,
            ProblemDifficulty difficulty,
            EntityStatus status
    );

}