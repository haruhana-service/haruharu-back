package org.kwakmunsu.haruhana.domain.submission.service;

import lombok.RequiredArgsConstructor;
import org.kwakmunsu.haruhana.domain.dailyproblem.entity.DailyProblem;
import org.kwakmunsu.haruhana.domain.dailyproblem.service.DailyProblemReader;
import org.kwakmunsu.haruhana.domain.submission.entity.Submission;
import org.kwakmunsu.haruhana.domain.submission.event.SubmissionCompletedEvent;
import org.kwakmunsu.haruhana.domain.submission.service.dto.response.SubmissionResponse;
import org.kwakmunsu.haruhana.domain.submission.service.dto.response.SubmissionResult;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class SubmissionService {

    private final DailyProblemReader dailyProblemReader;
    private final SubmissionManager submissionManager;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 문제 제출
     * - DailyProblem 조회 (본인 검증) <br>
     * - Submission 등록/업데이트 <br>
     * - DailyProblem을 풀이 완료 상태로 변경 <br>
     * - 제출 완료 이벤트 발행 (Streak 관리)
     *
     * @param dailyProblemId 오늘의 문제 ID
     * @param memberId 회원 ID
     * @param userAnswer 사용자가 제출한 답변

     * @return SubmissionResponse 제출 응답 DTO
     */
    @Transactional
    public SubmissionResponse submitSolution(Long dailyProblemId, Long memberId, String userAnswer) {
        DailyProblem dailyProblem = dailyProblemReader.find(dailyProblemId, memberId);

        // 답변 등록 또는 업데이트
        SubmissionResult result = submissionManager.submit(dailyProblem, userAnswer);

        // Streak 관리를 위한 제출 완료 이벤트 발행
        Submission submission = result.submission();
        eventPublisher.publishEvent(SubmissionCompletedEvent.of(
                memberId,
                submission.getId(),
                submission.isOnTime(),
                result.isFirstSubmission()
        ));

        return SubmissionResponse.of(submission, dailyProblem);
    }

}

