package org.kwakmunsu.haruhana.domain.submission.event;

import lombok.Builder;

/**
 * 문제 제출 완료 이벤트
 */
@Builder
public record SubmissionCompletedEvent(
        Long memberId,
        Long submissionId,
        boolean isOnTime, // 제시간 제출 여부 (스트릭 증가 조건)
        boolean isFirstSubmission // 최초 제출인지 (업데이트 아님)
) {

    public static SubmissionCompletedEvent of(
            Long memberId,
            Long submissionId,
            boolean isOnTime,
            boolean isFirstSubmission
    ) {
        return SubmissionCompletedEvent.builder()
                .memberId(memberId)
                .submissionId(submissionId)
                .isOnTime(isOnTime)
                .isFirstSubmission(isFirstSubmission)
                .build();
    }

}