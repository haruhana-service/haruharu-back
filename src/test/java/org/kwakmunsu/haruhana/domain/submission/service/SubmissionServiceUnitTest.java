package org.kwakmunsu.haruhana.domain.submission.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.time.LocalDateTime;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.kwakmunsu.haruhana.UnitTestSupport;
import org.kwakmunsu.haruhana.domain.category.entity.CategoryTopic;
import org.kwakmunsu.haruhana.domain.dailyproblem.DailyProblemFixture;
import org.kwakmunsu.haruhana.domain.dailyproblem.service.DailyProblemReader;
import org.kwakmunsu.haruhana.domain.member.MemberFixture;
import org.kwakmunsu.haruhana.domain.member.enums.Role;
import org.kwakmunsu.haruhana.domain.problem.ProblemFixture;
import org.kwakmunsu.haruhana.domain.submission.SubmissionFixture;
import org.kwakmunsu.haruhana.domain.submission.service.dto.response.SubmissionResponse;
import org.kwakmunsu.haruhana.domain.submission.service.dto.response.SubmissionResult;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;

class SubmissionServiceUnitTest extends UnitTestSupport {

    @Mock
    DailyProblemReader dailyProblemReader;

    @Mock
    SubmissionManager submissionManager;

    @Mock
    ApplicationEventPublisher eventPublisher;

    @InjectMocks
    SubmissionService submissionService;

    @Test
    void 답변을_시간_내_제출한다() {
        // given
        var member = MemberFixture.createMember(Role.ROLE_MEMBER);
        var problem = ProblemFixture.createProblem(CategoryTopic.create(1L, "알고리즘"));
        var dailyProblem = DailyProblemFixture.createDailyProblem(member, problem);

        var submission = SubmissionFixture.createSubmission(
                member,
                dailyProblem,
                "user answer",
                LocalDateTime.now()
        );
        var submissionResult = new SubmissionResult(submission, true /* isFirstSubmission */);

        given(dailyProblemReader.find(any(), any())).willReturn(dailyProblem);
        given(submissionManager.submit(any(), any())).willReturn(submissionResult);

        // when
        var submissionResponse = submissionService.submitSolution(dailyProblem.getId(), member.getId(), "user answer");

        // then
        Assertions.assertThat(submissionResponse).isNotNull().extracting(
                SubmissionResponse::submissionId,
                SubmissionResponse::dailyProblemId,
                SubmissionResponse::userAnswer,
                SubmissionResponse::isOnTime,
                SubmissionResponse::aiAnswer
        ).containsExactly(
                submission.getId(),
                dailyProblem.getId(),
                submission.getAnswer(),
                true,
                dailyProblem.getProblem().getAiAnswer()
        );
    }

    @Test
    void 답변을_늦게_제출한다() {
        // given
        var member = MemberFixture.createMember(Role.ROLE_MEMBER);
        var problem = ProblemFixture.createProblem(CategoryTopic.create(1L, "알고리즘"));
        var dailyProblem = DailyProblemFixture.createDailyProblem(member, problem);

        var submission = SubmissionFixture.createSubmission(
                member,
                dailyProblem,
                "user answer",
                LocalDateTime.now().plusDays(3)
        );
        var submissionResult = new SubmissionResult(submission, true /* isFirstSubmission */);

        given(dailyProblemReader.find(any(), any())).willReturn(dailyProblem);
        given(submissionManager.submit(any(), any())).willReturn(submissionResult);

        // when
        var submissionResponse = submissionService.submitSolution(dailyProblem.getId(), member.getId(), "user answer");

        // then
        Assertions.assertThat(submissionResponse).isNotNull().extracting(
                SubmissionResponse::submissionId,
                SubmissionResponse::dailyProblemId,
                SubmissionResponse::userAnswer,
                SubmissionResponse::isOnTime,
                SubmissionResponse::aiAnswer
        ).containsExactly(
                submission.getId(),
                dailyProblem.getId(),
                submission.getAnswer(),
                false,
                dailyProblem.getProblem().getAiAnswer()
        );
    }

}