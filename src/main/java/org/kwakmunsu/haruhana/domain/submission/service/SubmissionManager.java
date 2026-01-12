package org.kwakmunsu.haruhana.domain.submission.service;

import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.kwakmunsu.haruhana.domain.dailyproblem.entity.DailyProblem;
import org.kwakmunsu.haruhana.domain.submission.entity.Submission;
import org.kwakmunsu.haruhana.domain.submission.repository.SubmissionJpaRepository;
import org.kwakmunsu.haruhana.domain.submission.service.dto.response.SubmissionResult;
import org.kwakmunsu.haruhana.global.entity.EntityStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class SubmissionManager {

    private final SubmissionJpaRepository submissionJpaRepository;

    /**
     * 제출 등록 또는 업데이트
     * - 이미 제출했다면 답변 업데이트
     * - 제출 기록이 없다면 새로 생성
     * - 할당 날짜 내 제출: isOnTime = true (스트릭 증가 가능)
     * - 할당 날짜 지난 후 제출: isOnTime = false (스트릭 증가 안됨)
     *
     * @return SubmissionResult (제출 정보와 최초 제출 여부)
     */
    @Transactional
    public SubmissionResult submit(DailyProblem dailyProblem, String userAnswer) {
        Optional<Submission> existingSubmission = submissionJpaRepository.findByMemberIdAndDailyProblemIdAndStatus(
                dailyProblem.getMember().getId(),
                dailyProblem.getId(),
                EntityStatus.ACTIVE
        );

        if (existingSubmission.isPresent()) {
            Submission submission = existingSubmission.get();
            submission.updateAnswer(userAnswer);

            return new SubmissionResult(submission, false /*isFirstSubmission */);
        } else {
            Submission saved = submissionJpaRepository.save(Submission.create(
                    dailyProblem.getMember(),
                    dailyProblem,
                    userAnswer,
                    LocalDateTime.now()
            ));
            dailyProblem.markAsSolved();

            return new SubmissionResult(saved, true /*isFirstSubmission */);
        }
    }

}