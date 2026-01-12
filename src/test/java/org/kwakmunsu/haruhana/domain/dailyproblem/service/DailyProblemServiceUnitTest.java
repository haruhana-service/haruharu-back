package org.kwakmunsu.haruhana.domain.dailyproblem.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.kwakmunsu.haruhana.UnitTestSupport;
import org.kwakmunsu.haruhana.domain.dailyproblem.entity.DailyProblem;
import org.kwakmunsu.haruhana.domain.dailyproblem.service.dto.response.DailyProblemResponse;
import org.kwakmunsu.haruhana.domain.member.MemberFixture;
import org.kwakmunsu.haruhana.domain.member.enums.Role;
import org.kwakmunsu.haruhana.domain.problem.ProblemFixture;
import org.kwakmunsu.haruhana.global.support.error.ErrorType;
import org.kwakmunsu.haruhana.global.support.error.HaruHanaException;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class DailyProblemServiceUnitTest extends UnitTestSupport {

    @Mock
    DailyProblemReader dailyProblemReader;

    @InjectMocks
    DailyProblemService dailyProblemService;

    @Test
    void 오늘의_문제를_조회한다() {
        // given
        var member = MemberFixture.createMember(Role.ROLE_MEMBER);
        var problem = ProblemFixture.creatProblem();
        LocalDate today = LocalDate.now();
        var dailyProblem = DailyProblem.create(member, problem, today);
        var memberId = 1L;

        given(dailyProblemReader.findDailyProblemByMember(any())).willReturn(dailyProblem);

        // when
        var dailyProblemResponse = dailyProblemService.getTodayProblem(memberId);

        // then
        assertThat(dailyProblemResponse).isNotNull()
                .extracting(
                        DailyProblemResponse::title,
                        DailyProblemResponse::description,
                        DailyProblemResponse::difficulty,
                        DailyProblemResponse::categoryTopicName,
                        DailyProblemResponse::isSolved
                ).containsExactly(
                        problem.getTitle(),
                        problem.getDescription(),
                        problem.getDifficulty().name(),
                        problem.getCategoryTopic().getName(),
                        false
                );
    }

    @Test
    void 오늘의_문제가_존재하지_않는다() {
        // given
        var memberId = 1L;

        willThrow(new HaruHanaException(ErrorType.NOT_FOUND_DAILY_PROBLEM))
                .given(dailyProblemReader).findDailyProblemByMember(any());

        // when & then
        assertThatThrownBy(() -> dailyProblemService.getTodayProblem(memberId))
                .isInstanceOf(HaruHanaException.class)
                .hasMessage(ErrorType.NOT_FOUND_DAILY_PROBLEM.getMessage());
    }

}