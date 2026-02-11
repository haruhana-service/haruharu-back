package org.kwakmunsu.haruhana.domain.streak.service;

import jakarta.persistence.OptimisticLockException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kwakmunsu.haruhana.domain.streak.entity.Streak;
import org.kwakmunsu.haruhana.domain.streak.service.dto.response.StreakResponse;
import org.kwakmunsu.haruhana.domain.streak.service.dto.response.WeeklySolvedStatusResponse;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class StreakService {

    private final StreakManager streakManager;
    private final StreakReader streakReader;

    @Retryable(
            retryFor = {
                    OptimisticLockException.class,
                    OptimisticLockingFailureException.class
            },
            maxAttempts = 3,
            backoff = @Backoff(delay = 100)
    )
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void increaseWithRetry(Long memberId) {
        streakManager.increase(memberId);

        log.info("[StreakService] 스트릭 증가 완료 - memberId: {}", memberId);
    }

    // NOTE: Caching 적용 필요성 검토
    @Transactional(readOnly = true)
    public StreakResponse getStreak(Long memberId) {
        Streak streak = streakReader.getByMemberId(memberId);
        List<WeeklySolvedStatusResponse> weeklySolvedStatus = streakReader.getWeeklySolvedStatus(memberId);

        log.debug("[StreakService] 현재 스트릭 조회 완료 - memberId: {}, currentStreak: {}", memberId, streak.getCurrentStreak());

        return StreakResponse.from(streak, weeklySolvedStatus);
    }

}