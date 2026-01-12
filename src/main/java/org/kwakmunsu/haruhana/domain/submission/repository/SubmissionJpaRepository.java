package org.kwakmunsu.haruhana.domain.submission.repository;

import java.util.Optional;
import org.kwakmunsu.haruhana.domain.submission.entity.Submission;
import org.kwakmunsu.haruhana.global.entity.EntityStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionJpaRepository extends JpaRepository<Submission, Long> {

    Optional<Submission> findByMemberIdAndDailyProblemIdAndStatus(Long memberId, Long dailyProblemId, EntityStatus status);

}