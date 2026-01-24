package org.kwakmunsu.haruhana.domain.dailyproblem.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.kwakmunsu.haruhana.domain.dailyproblem.entity.DailyProblem;
import org.kwakmunsu.haruhana.domain.dailyproblem.repository.DailyProblemJpaRepository;
import org.kwakmunsu.haruhana.global.entity.EntityStatus;
import org.kwakmunsu.haruhana.global.support.error.ErrorType;
import org.kwakmunsu.haruhana.global.support.error.HaruHanaException;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DailyProblemReader {

    private final DailyProblemJpaRepository dailyProblemJpaRepository;

    public DailyProblem findDailyProblemByMember(Long memberId) {
        return dailyProblemJpaRepository.findByMemberIdAndAssignedAtAndStatus(
                memberId,
                LocalDate.now(),
                EntityStatus.ACTIVE
        ).orElseThrow(() -> new HaruHanaException(ErrorType.NOT_FOUND_DAILY_PROBLEM));
    }

    public DailyProblem find(Long id, Long memberId) {
        return dailyProblemJpaRepository.findByIdAndMemberIdAndStatus(id, memberId, EntityStatus.ACTIVE)
                .orElseThrow(() -> new HaruHanaException(ErrorType.NOT_FOUND_DAILY_PROBLEM));
    }

    public Optional<DailyProblem> findDailyProblem(LocalDate assignedAt, Long memberId) {
        if (assignedAt == null) {
            assignedAt = LocalDate.now();
        }

        return dailyProblemJpaRepository.findByMemberIdAndAssignedAtAndStatus(
                memberId,
                assignedAt,
                EntityStatus.ACTIVE
        );
    }

    public List<Long> findUnsolvedMember(LocalDate targetDate) {
        return dailyProblemJpaRepository.findUnsolvedMemberIds(targetDate, EntityStatus.ACTIVE);
    }

}