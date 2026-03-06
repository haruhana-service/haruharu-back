package org.kwakmunsu.haruhana.domain.problem.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.kwakmunsu.haruhana.domain.problem.repository.ProblemJpaRepository;
import org.kwakmunsu.haruhana.global.entity.EntityStatus;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ProblemReader {

    private final ProblemJpaRepository problemJpaRepository;

    public long countByCreatedAtToday() {
        LocalDate today = LocalDateTime.now().toLocalDate();

        return problemJpaRepository.countByProblemAtAndStatus(today, EntityStatus.ACTIVE);
    }

}