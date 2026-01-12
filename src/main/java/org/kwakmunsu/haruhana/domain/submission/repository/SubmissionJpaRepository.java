package org.kwakmunsu.haruhana.domain.submission.repository;

import java.util.Optional;
import org.kwakmunsu.haruhana.domain.submission.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionJpaRepository extends JpaRepository<Submission, Long> {

    Optional<Submission> findByMemberIdAndDailyProblemId(Long memberId, Long dailyProblemId);

}