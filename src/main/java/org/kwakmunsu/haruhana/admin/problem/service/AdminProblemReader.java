package org.kwakmunsu.haruhana.admin.problem.service;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.kwakmunsu.haruhana.admin.problem.service.dto.AdminProblemPreviewResponse;
import org.kwakmunsu.haruhana.domain.problem.entity.Problem;
import org.kwakmunsu.haruhana.domain.problem.repository.ProblemJpaRepository;
import org.kwakmunsu.haruhana.global.entity.EntityStatus;
import org.kwakmunsu.haruhana.global.support.OffsetLimit;
import org.kwakmunsu.haruhana.global.support.response.PageResponse;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AdminProblemReader {

    private final ProblemJpaRepository problemJpaRepository;

    public PageResponse<AdminProblemPreviewResponse> findProblems(LocalDate date, OffsetLimit offsetLimit) {
        if (date == null) {
            date = LocalDate.now();
        }

        List<Problem> problems = problemJpaRepository.findProblemsByProblemAt(
                date,
                offsetLimit.limit() + 1,
                offsetLimit.offset(),
                EntityStatus.ACTIVE
        );

        boolean hasNext = false;
        if (problems.size() > offsetLimit.limit()) {
            problems.removeLast();
            hasNext = true;
        }

        List<AdminProblemPreviewResponse> responses = AdminProblemPreviewResponse.from(problems);

        return PageResponse.<AdminProblemPreviewResponse>builder()
                .contents(responses)
                .hasNext(hasNext)
                .build();
    }

}