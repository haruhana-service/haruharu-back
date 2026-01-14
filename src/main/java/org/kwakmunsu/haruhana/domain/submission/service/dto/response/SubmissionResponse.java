package org.kwakmunsu.haruhana.domain.submission.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import org.kwakmunsu.haruhana.domain.dailyproblem.entity.DailyProblem;
import org.kwakmunsu.haruhana.domain.submission.entity.Submission;

@Schema(description = "문제 제출 응답 DTO")
@Builder
public record SubmissionResponse(
        @Schema(description = "제출 ID", example = "1")
        Long submissionId,

        @Schema(description = "오늘의 문제 ID", example = "1")
        Long dailyProblemId,

        @Schema(description = "사용자 답변", example = "Inversion of Control은...")
        String userAnswer,

        @Schema(description = "제출 시각")
        LocalDateTime submittedAt,

        @Schema(description = "제시간 제출 여부 (스트릭 증가 가능)", example = "true")
        boolean isOnTime,

        @Schema(description = "AI 모범 답안", example = "Inversion of Control (IoC)는...")
        String aiAnswer
) {

    public static SubmissionResponse of(Submission submission, DailyProblem dailyProblem) {
        return SubmissionResponse.builder()
                .submissionId(submission.getId())
                .dailyProblemId(dailyProblem.getId())
                .userAnswer(submission.getAnswer())
                .submittedAt(submission.getSubmittedAt())
                .isOnTime(submission.isOnTime())
                .aiAnswer(dailyProblem.getProblem().getAiAnswer())
                .build();
    }

}

