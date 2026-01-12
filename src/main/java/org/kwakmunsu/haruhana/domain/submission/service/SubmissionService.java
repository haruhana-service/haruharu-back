package org.kwakmunsu.haruhana.domain.submission.service;

import lombok.RequiredArgsConstructor;
import org.kwakmunsu.haruhana.domain.dailyproblem.entity.DailyProblem;
import org.kwakmunsu.haruhana.domain.dailyproblem.service.DailyProblemReader;
import org.kwakmunsu.haruhana.domain.submission.entity.Submission;
import org.kwakmunsu.haruhana.domain.submission.service.dto.response.SubmissionResponse;
import org.kwakmunsu.haruhana.domain.submission.service.dto.response.SubmissionResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class SubmissionService {

    private final DailyProblemReader dailyProblemReader;
    private final SubmissionManager submissionManager;

    /**
     * 문제 제출
     * - DailyProblem 조회 (본인 검증)
     * - Submission 등록/업데이트
     * - DailyProblem을 풀이 완료 상태로 변경
     * - 제출 완료 이벤트 발행 (Streak 관리)
     */
    @Transactional
    public SubmissionResponse submitSolution(Long dailyProblemId, Long memberId, String userAnswer) {
        DailyProblem dailyProblem = dailyProblemReader.find(dailyProblemId, memberId);

        // Submission 등록 또는 업데이트
        SubmissionResult result = submissionManager.submit(dailyProblem, userAnswer);
        Submission submission = result.submission();

        return SubmissionResponse.of(submission, dailyProblem);
    }

}

