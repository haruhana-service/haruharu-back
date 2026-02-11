package org.kwakmunsu.haruhana.domain.streak.service;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.kwakmunsu.haruhana.domain.dailyproblem.entity.DailyProblem;
import org.kwakmunsu.haruhana.domain.dailyproblem.repository.DailyProblemJpaRepository;
import org.kwakmunsu.haruhana.domain.streak.entity.Streak;
import org.kwakmunsu.haruhana.domain.streak.repository.StreakJpaRepository;
import org.kwakmunsu.haruhana.domain.streak.service.dto.response.WeeklySolvedStatusResponse;
import org.kwakmunsu.haruhana.global.entity.EntityStatus;
import org.kwakmunsu.haruhana.global.support.error.ErrorType;
import org.kwakmunsu.haruhana.global.support.error.HaruHanaException;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class StreakReader {

    private final StreakJpaRepository streakJpaRepository;
    private final DailyProblemJpaRepository dailyProblemJpaRepository;

    public Streak getByMemberId(Long memberId) {
        return streakJpaRepository.findByMemberIdAndStatus(memberId, EntityStatus.ACTIVE)
                .orElseThrow(() -> new HaruHanaException(ErrorType.NOT_FOUND_STREAK));
    }

    /**
     *  회원의 최근 7일간 풀이 현황을 조회한다.
     *  @param memberId 회원 ID
     *
     *  @return List<WeeklySolvedStatusResponse> 최근 7일간 풀이 현황 리스트
    **/
    public List<WeeklySolvedStatusResponse> getWeeklySolvedStatus(Long memberId) {
        LocalDate today = LocalDate.now();
        LocalDate sevenDaysAgo = today.minusDays(6);

        List<DailyProblem> dailyProblems = dailyProblemJpaRepository.findSolvedByMemberIdAndDateRange(
                memberId,
                sevenDaysAgo,
                today,
                EntityStatus.ACTIVE
        );

        return WeeklySolvedStatusResponse.from(dailyProblems, today);
    }

}