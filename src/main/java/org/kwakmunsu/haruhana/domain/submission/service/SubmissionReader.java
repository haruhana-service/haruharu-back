package org.kwakmunsu.haruhana.domain.submission.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.kwakmunsu.haruhana.domain.submission.entity.Submission;
import org.kwakmunsu.haruhana.domain.submission.repository.SubmissionJpaRepository;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SubmissionReader {

    private final SubmissionJpaRepository submissionJpaRepository;

    /**
     * Member ID와 DailyProblem ID로 제출 정보 조회 (보안 검증용)
     */
    public Optional<Submission> findByMemberIdAndDailyProblemId(Long memberId, Long dailyProblemId) {
        return submissionJpaRepository.findByMemberIdAndDailyProblemId(memberId, dailyProblemId);
    }

}