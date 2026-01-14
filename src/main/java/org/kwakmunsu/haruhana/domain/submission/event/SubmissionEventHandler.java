package org.kwakmunsu.haruhana.domain.submission.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kwakmunsu.haruhana.domain.member.entity.Member;
import org.kwakmunsu.haruhana.domain.streak.service.StreakManager;
import org.kwakmunsu.haruhana.domain.streak.service.StreakService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 제출 완료 이벤트 핸들러 - Streak 증가/유지 로직 처리 - Optimistic Lock 충돌 시 자동 재시도 (최대 3회)
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class SubmissionEventHandler {

    private final StreakService streakService;
    private final StreakManager streakManager;

    /**
     * 제출 완료 이벤트 처리 <br>
     * - 최초 제출이고, 제시간에 제출한 경우에만 스트릭 증가 <br>
     * - 답변 업데이트의 경우에는 스트릭 증가 안함 <br>
     * - Optimistic Lock 충돌 시 최대 3회 재시도 (100ms 간격)
     *
     * @param event 제출 완료 이벤트
     */
    @Async
    @EventListener
    public void handleSubmissionCompleted(SubmissionCompletedEvent event) {
        // 최초 제출이고 제시간에 제출한 경우에만 스트릭 증가
        if (event.isFirstSubmission() && event.isOnTime()) {
            streakService.increaseWithRetry(event.memberId());
        } else {
            log.info("[SubmissionEventHandler] 스트릭 증가 조건 미충족 최초 제출: {} or 시간 내 제출: {}", event.isFirstSubmission(), event.isOnTime());
        }
    }

}