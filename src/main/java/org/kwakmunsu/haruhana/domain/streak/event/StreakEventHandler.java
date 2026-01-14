package org.kwakmunsu.haruhana.domain.streak.event;

import lombok.RequiredArgsConstructor;
import org.kwakmunsu.haruhana.domain.streak.service.StreakManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class StreakEventHandler {

    private final StreakManager streakManager;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMemberRegistered(StreakCreateEvent event) {
        streakManager.create(event.member());
    }

}