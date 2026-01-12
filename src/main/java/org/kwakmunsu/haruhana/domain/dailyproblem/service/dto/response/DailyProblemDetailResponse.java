package org.kwakmunsu.haruhana.domain.dailyproblem.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;
import org.kwakmunsu.haruhana.domain.dailyproblem.entity.DailyProblem;
import org.kwakmunsu.haruhana.domain.problem.entity.Problem;
import org.kwakmunsu.haruhana.domain.submission.entity.Submission;

@Schema(description = "데일리 문제 상세 응답 DTO")
@Builder
public record DailyProblemDetailResponse(
        @Schema(description = "Daily Problem ID", example = "1")
        Long id,

        @Schema(description = "문제 난이도", example = "EASY")
        String difficulty,

        @Schema(description = "문제 카테고리 토픽 이름", example = "Java")
        String categoryTopic,

        @Schema(description = "문제 할당 날짜")
        LocalDate assignedAt,

        @Schema(description = "문제 제목", example = "Spring IOC/DI")
        String title,

        @Schema(description = "문제 설명", example = "Explain the concept of Inversion of Control...")
        String description,

        @Schema(description = "사용자 답변(답변 미체출 시 null)", example = "Inversion of Control is...")
        String userAnswer,

        @Schema(description = "답변 제출 시각(답변 미체출 시 null)")
        LocalDateTime submittedAt,

        @Schema(description = "AI 답안(답변 미체출 시 null)", example = "Inversion of Control (IoC) is...")
        String aiAnswer
) {

    public static DailyProblemDetailResponse of(DailyProblem dailyProblem, Submission submission) {
        Problem problem = dailyProblem.getProblem();

        return DailyProblemDetailResponse.builder()
                .id(dailyProblem.getId())
                .assignedAt(dailyProblem.getAssignedAt())
                .difficulty(problem.getDifficulty().name())
                .categoryTopic(problem.getCategoryTopic().getName())
                .title(problem.getTitle())
                .description(problem.getDescription())
                .aiAnswer(submission != null ? problem.getAiAnswer() : null)
                .userAnswer(submission != null ? submission.getAnswer() : null)
                .submittedAt(submission != null ? submission.getSubmittedAt() : null)
                .build();
    }

}