package org.kwakmunsu.haruhana.domain.submission.event;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.kwakmunsu.haruhana.UnitTestSupport;
import org.kwakmunsu.haruhana.domain.streak.service.StreakService;
import org.mockito.InjectMocks;
import org.mockito.Mock;

/**
 * SubmissionEventHandler 유닛 테스트
 * - 이벤트 핸들러 로직만 검증
 * - StreakService 호출 여부만 확인 (재시도 로직은 StreakService에서 테스트)
 */
class SubmissionEventHandlerUnitTest extends UnitTestSupport {

    @Mock
    StreakService streakService;

    @InjectMocks
    SubmissionEventHandler submissionEventHandler;

    @Test
    void 최초_제출이고_제시간_제출이면_스트릭을_증가시킨다() {
        // given
        var event = SubmissionCompletedEvent.of(
                1L,                     // memberId
                1L,                     // submissionId
                true,                   // isOnTime
                true                    // isFirstSubmission
        );

        // when
        submissionEventHandler.handleSubmissionCompleted(event);

        // then
        verify(streakService, times(1)).increaseWithRetry(1L);
    }

    @Test
    void 최초_제출이_아니면_스트릭을_증가시키지_않는다() {
        // given - 재제출 (답변 업데이트)
        var event = SubmissionCompletedEvent.of(
                1L,
                1L,
                true,                   // isOnTime
                false                   // isFirstSubmission = false (재제출)
        );

        // when
        submissionEventHandler.handleSubmissionCompleted(event);

        // then
        verify(streakService, never()).increaseWithRetry(1L);
    }

    @Test
    void 제시간_제출이_아니면_스트릭을_증가시키지_않는다() {
        // given - 늦은 제출
        var event = SubmissionCompletedEvent.of(
                1L,
                1L,
                false,                  // isOnTime = false (늦은 제출)
                true                    // isFirstSubmission
        );

        // when
        submissionEventHandler.handleSubmissionCompleted(event);

        // then
        verify(streakService, never()).increaseWithRetry(1L);
    }

    @Test
    void 최초_제출도_아니고_제시간도_아니면_스트릭을_증가시키지_않는다() {
        // given
        var event = SubmissionCompletedEvent.of(
                1L,
                1L,
                false,                  // isOnTime = false
                false                   // isFirstSubmission = false
        );

        // when
        submissionEventHandler.handleSubmissionCompleted(event);

        // then
        verify(streakService, never()).increaseWithRetry(1L);
    }

    @Test
    void 여러_회원이_각각_제출하면_각각_스트릭이_증가한다() {
        // given
        var event1 = SubmissionCompletedEvent.of(1L, 1L, true, true);
        var event2 = SubmissionCompletedEvent.of(2L, 2L, true, true);
        var event3 = SubmissionCompletedEvent.of(3L, 3L, true, true);

        // when
        submissionEventHandler.handleSubmissionCompleted(event1);
        submissionEventHandler.handleSubmissionCompleted(event2);
        submissionEventHandler.handleSubmissionCompleted(event3);

        // then
        verify(streakService, times(1)).increaseWithRetry(1L);
        verify(streakService, times(1)).increaseWithRetry(2L);
        verify(streakService, times(1)).increaseWithRetry(3L);
    }

}