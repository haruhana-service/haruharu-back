package org.kwakmunsu.haruhana.domain.problem.service.dto;

import lombok.Builder;

@Builder
public record ProblemResponse(
        String title,
        String description,
        String aiAnswer
) {

}