package org.kwakmunsu.haruhana.domain.streak.service;

import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

}