package org.kwakmunsu.haruhana.domain.problem.service.dto;

import lombok.Builder;
import org.kwakmunsu.haruhana.domain.problem.enums.ProblemDifficulty;

/**
 * 카테고리와 난이도를 그룹화하는 키
 */
@Builder
public record ProblemGenerationKey(
        Long categoryTopicId,
        String categoryTopicName,
        ProblemDifficulty difficulty
) {

    public static ProblemGenerationKey of(Long categoryTopicId, String categoryTopicName, ProblemDifficulty difficulty) {
        return ProblemGenerationKey.builder()
                .categoryTopicId(categoryTopicId)
                .categoryTopicName(categoryTopicName)
                .difficulty(difficulty)
                .build();
    }

}

