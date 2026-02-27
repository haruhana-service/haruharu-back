package org.kwakmunsu.haruhana.admin.problem.service;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.kwakmunsu.haruhana.admin.problem.service.dto.AdminProblemPreviewResponse;
import org.kwakmunsu.haruhana.global.support.OffsetLimit;
import org.kwakmunsu.haruhana.global.support.response.PageResponse;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AdminProblemService {

    private final AdminProblemReader adminProblemReader;

    public PageResponse<AdminProblemPreviewResponse> findProblems(LocalDate date, OffsetLimit offsetLimit) {
        return adminProblemReader.findProblems(date, offsetLimit);
    }

}