package org.kwakmunsu.haruhana.domain.scheduler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.kwakmunsu.haruhana.UnitTestSupport;
import org.kwakmunsu.haruhana.domain.problem.service.ProblemGenerator;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class ProblemSchedulerUnitTest extends UnitTestSupport {

    @Mock
    ProblemGenerator problemGenerator;

    @InjectMocks
    ProblemScheduler problemScheduler;

    @Test
    void 매일_자정에_문제_생성_스케줄러가_정상_실행된다() {
        // given
        doNothing().when(problemGenerator).generateProblem(any(LocalDate.class));

        // when
        problemScheduler.generateDailyProblems();

        // then
        verify(problemGenerator, times(1)).generateProblem(any(LocalDate.class));
    }

    @Test
    void 문제_생성_중_예외가_발생해도_스케줄러는_정상_종료된다() {
        // given
        doThrow(new RuntimeException("문제 생성 실패"))
                .when(problemGenerator).generateProblem(any(LocalDate.class));

        // when
        problemScheduler.generateDailyProblems();

        // then
        verify(problemGenerator, times(1)).generateProblem(any(LocalDate.class));
        // 예외가 발생해도 스케줄러는 종료되지 않음 (로그만 출력)
    }

    @Test
    void targetDate는_현재_날짜의_다음_날이다() {
        // given
        LocalDate expectedDate = LocalDate.now().plusDays(1);
        doNothing().when(problemGenerator).generateProblem(expectedDate);

        // when
        problemScheduler.generateDailyProblems();

        // then
        verify(problemGenerator, times(1)).generateProblem(expectedDate);
    }

}