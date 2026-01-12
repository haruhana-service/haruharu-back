package org.kwakmunsu.haruhana.domain.dailyproblem.service;

import lombok.RequiredArgsConstructor;
import org.kwakmunsu.haruhana.domain.dailyproblem.entity.DailyProblem;
import org.kwakmunsu.haruhana.domain.dailyproblem.service.dto.response.DailyProblemDetailResponse;
import org.kwakmunsu.haruhana.domain.dailyproblem.service.dto.response.TodayProblemResponse;
import org.kwakmunsu.haruhana.domain.submission.entity.Submission;
import org.kwakmunsu.haruhana.domain.submission.service.SubmissionReader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class DailyProblemService {

    private final DailyProblemReader dailyProblemReader;
    private final SubmissionReader submissionReader;

    /**
     * 오늘의 문제 조회
     * @param memberId 회원 ID
     */
    @Transactional(readOnly = true)
    public TodayProblemResponse getTodayProblem(Long memberId) {
        DailyProblem dailyProblem = dailyProblemReader.findDailyProblemByMember(memberId);

        return TodayProblemResponse.from(dailyProblem);
    }

    /**
     * 문제 상세 조회
     * @param dailyProblemId 오늘의 문제 ID
     * @param memberId 회원 ID
     */
    @Transactional(readOnly = true)
    public DailyProblemDetailResponse findDailyProblem(Long dailyProblemId, Long memberId) {
        DailyProblem dailyProblem = dailyProblemReader.find(dailyProblemId, memberId);

        Submission submission = submissionReader.findByMemberIdAndDailyProblemId(memberId, dailyProblemId)
                .orElse(null);

        return DailyProblemDetailResponse.of(dailyProblem, submission);
    }
}