package org.kwakmunsu.haruhana.domain.member.service.dto.request;

import lombok.Builder;
import org.kwakmunsu.haruhana.domain.problem.enums.ProblemDifficulty;

@Builder
public record NewPreference(
        Long categoryTopicId,
        ProblemDifficulty difficulty
) {

}