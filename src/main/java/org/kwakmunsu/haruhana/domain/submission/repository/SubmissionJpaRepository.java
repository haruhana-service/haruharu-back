package org.kwakmunsu.haruhana.domain.submission.repository;

import java.util.Optional;
import org.kwakmunsu.haruhana.domain.submission.entity.Submission;
import org.kwakmunsu.haruhana.global.entity.EntityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SubmissionJpaRepository extends JpaRepository<Submission, Long> {

    Optional<Submission> findByMemberIdAndDailyProblemIdAndStatus(Long memberId, Long dailyProblemId, EntityStatus status);

    @Modifying
    @Query("UPDATE Submission s SET s.status = :status WHERE s.member.id = :memberId")
    void softDeleteByMemberId(@Param("memberId") Long memberId, @Param("status") EntityStatus status);

}