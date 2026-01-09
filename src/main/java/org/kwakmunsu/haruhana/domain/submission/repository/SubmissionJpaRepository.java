package org.kwakmunsu.haruhana.domain.submission.repository;

import org.kwakmunsu.haruhana.domain.submission.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionJpaRepository extends JpaRepository<Submission, Long> {

}