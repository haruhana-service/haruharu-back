package org.kwakmunsu.haruhana.domain.problem.service.dto;

import lombok.Builder;

@Builder
public record ProblemResponse(
        String title,
        String description,
        String aiAnswer
) {

    public boolean isValid() {
        return title != null && !title.isBlank() &&
               description != null && !description.isBlank() &&
               aiAnswer != null && !aiAnswer.isBlank();
    }

}