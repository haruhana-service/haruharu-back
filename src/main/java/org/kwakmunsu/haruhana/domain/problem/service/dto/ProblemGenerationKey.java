package org.kwakmunsu.haruhana.domain.problem.service.dto;

import org.kwakmunsu.haruhana.domain.problem.enums.ProblemDifficulty;

/**
 * 카테고리와 난이도를 그룹화하는 키
 */
public record ProblemGenerationKey(
        Long categoryTopicId,
        String categoryTopicName,
        ProblemDifficulty difficulty
) {

    public static ProblemGenerationKey of(Long categoryTopicId, String categoryTopicName, ProblemDifficulty difficulty) {
        return new ProblemGenerationKey(categoryTopicId, categoryTopicName, difficulty);
    }

}

