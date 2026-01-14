package org.kwakmunsu.haruhana.domain.submission.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.kwakmunsu.haruhana.UnitTestSupport;
import org.kwakmunsu.haruhana.domain.category.CategoryTopicFixture;
import org.kwakmunsu.haruhana.domain.dailyproblem.DailyProblemFixture;
import org.kwakmunsu.haruhana.domain.member.MemberFixture;
import org.kwakmunsu.haruhana.domain.member.entity.Member;
import org.kwakmunsu.haruhana.domain.member.enums.Role;
import org.kwakmunsu.haruhana.domain.problem.ProblemFixture;
import org.kwakmunsu.haruhana.domain.submission.SubmissionFixture;
import org.kwakmunsu.haruhana.domain.submission.entity.Submission;
import org.kwakmunsu.haruhana.domain.submission.repository.SubmissionJpaRepository;
import org.kwakmunsu.haruhana.domain.submission.service.dto.response.SubmissionResult;
import org.kwakmunsu.haruhana.global.entity.EntityStatus;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class SubmissionManagerUnitTest extends UnitTestSupport {

    @Mock
    SubmissionJpaRepository submissionJpaRepository;

    @InjectMocks
    SubmissionManager submissionManager;

    @Test
    void 최초_제출_시_새로운_제출을_생성하고_DailyProblem을_풀이_완료_상태로_변경한다() {
        // given
        var member = MemberFixture.createMember(Role.ROLE_MEMBER);
        var categoryTopic = CategoryTopicFixture.createCategoryTopic();
        var problem = ProblemFixture.createProblem(categoryTopic);
        var dailyProblem = DailyProblemFixture.createUnsolvedDailyProblem(1L, member, problem);

        var userAnswer = "사용자 답변입니다.";
        var savedSubmission = SubmissionFixture.createSubmission(member, dailyProblem, userAnswer);

        given(submissionJpaRepository.findByMemberIdAndDailyProblemIdAndStatus(
                member.getId(),
                dailyProblem.getId(),
                EntityStatus.ACTIVE
        )).willReturn(Optional.empty());

        given(submissionJpaRepository.save(any())).willReturn(savedSubmission);

        assertThat(dailyProblem.isSolved()).isFalse(); // 제출 전에는 풀이 완료 아님

        // when
        SubmissionResult result = submissionManager.submit(dailyProblem, userAnswer);

        // then
        assertThat(result).isNotNull().extracting(
                SubmissionResult::submission,
                SubmissionResult::isFirstSubmission
        ).containsExactly(
                savedSubmission,
                true // 최초 제출
        );
        assertThat(dailyProblem.isSolved()).isTrue(); // DailyProblem이 풀이 완료 상태로 변경됨

        verify(submissionJpaRepository, times(1)).save(any());
    }

    @Test
    void 이미_제출한_경우_답변을_업데이트하고_최초_제출이_아님을_반환한다() {
        // given
        var member = MemberFixture.createMember(Role.ROLE_MEMBER);
        var categoryTopic = CategoryTopicFixture.createCategoryTopic();
        var problem = ProblemFixture.createProblem(categoryTopic);
        var dailyProblem = DailyProblemFixture.createSolvedDailyProblem(1L, member, problem);

        var oldAnswer = "기존 답변";
        var newAnswer = "수정된 답변";
        var existingSubmission = SubmissionFixture.createSubmission(1L, member, dailyProblem, oldAnswer);

        given(submissionJpaRepository.findByMemberIdAndDailyProblemIdAndStatus(
                member.getId(),
                dailyProblem.getId(),
                EntityStatus.ACTIVE
        )).willReturn(Optional.of(existingSubmission));

        // when
        SubmissionResult result = submissionManager.submit(dailyProblem, newAnswer);

        // then
        assertThat(result).isNotNull().extracting(
                SubmissionResult::submission,
                SubmissionResult::isFirstSubmission
        ).containsExactly(
                existingSubmission,
                false // 최초 제출 아님
        );
        assertThat(existingSubmission.getAnswer()).isEqualTo(newAnswer); // 답변이 업데이트됨

        verify(submissionJpaRepository, never()).save(any()); // save 호출 안됨
    }

    @Test
    void 할당_날짜_내_제출_시_isOnTime이_true로_설정된다() {
        // given
        var member = MemberFixture.createMember(Role.ROLE_MEMBER);
        var categoryTopic = CategoryTopicFixture.createCategoryTopic();
        var problem = ProblemFixture.createProblem(categoryTopic);
        var dailyProblem = DailyProblemFixture.createUnsolvedDailyProblem(1L, member, problem);

        var userAnswer = "제시간 제출";
        var onTimeSubmission = SubmissionFixture.createSubmission(member, dailyProblem, userAnswer, LocalDateTime.now());

        given(submissionJpaRepository.findByMemberIdAndDailyProblemIdAndStatus(
                member.getId(),
                dailyProblem.getId(),
                EntityStatus.ACTIVE
        )).willReturn(Optional.empty());

        given(submissionJpaRepository.save(any(Submission.class))).willReturn(onTimeSubmission);

        // when
        SubmissionResult result = submissionManager.submit(dailyProblem, userAnswer);

        // then
        assertThat(result.submission().isOnTime()).isTrue(); // 제시간 제출
        assertThat(result.isFirstSubmission()).isTrue();
    }

    @Test
    void 할당_날짜가_지난_후_제출_시_isOnTime이_false로_설정된다() {
        // given
        var member = MemberFixture.createMember(Role.ROLE_MEMBER);
        var categoryTopic = CategoryTopicFixture.createCategoryTopic();
        var problem = ProblemFixture.createProblem(categoryTopic);
        var dailyProblem = DailyProblemFixture.createUnsolvedDailyProblem(1L, member, problem);

        var userAnswer = "제시간 제출";
        var onTimeSubmission = SubmissionFixture.createSubmission(member, dailyProblem, userAnswer, LocalDateTime.now().plusDays(3));

        given(submissionJpaRepository.findByMemberIdAndDailyProblemIdAndStatus(
                member.getId(),
                dailyProblem.getId(),
                EntityStatus.ACTIVE
        )).willReturn(Optional.empty());

        given(submissionJpaRepository.save(any(Submission.class))).willReturn(onTimeSubmission);

        // when
        SubmissionResult result = submissionManager.submit(dailyProblem, userAnswer);

        // then
        assertThat(result.submission().isOnTime()).isFalse(); // 늦은 제출
        assertThat(result.isFirstSubmission()).isTrue();
    }

    @Test
    void 여러_회원이_각각_제출할_수_있다() {
        // given
        var member1 = MemberFixture.createMember(Role.ROLE_MEMBER);
        var member2 = createMemberWithId(2L);
        var categoryTopic = CategoryTopicFixture.createCategoryTopic();
        var problem = ProblemFixture.createProblem(categoryTopic);
        var dailyProblem1 = DailyProblemFixture.createUnsolvedDailyProblem(1L, member1, problem);
        var dailyProblem2 = DailyProblemFixture.createUnsolvedDailyProblem(2L, member2, problem);

        var userAnswer1 = "회원1 답변";
        var userAnswer2 = "회원2 답변";
        var submission1 = SubmissionFixture.createSubmission(1L, member1, dailyProblem1, userAnswer1);
        var submission2 = SubmissionFixture.createSubmission(2L, member2, dailyProblem2, userAnswer2);

        given(submissionJpaRepository.findByMemberIdAndDailyProblemIdAndStatus(
                member1.getId(),
                dailyProblem1.getId(),
                EntityStatus.ACTIVE
        )).willReturn(Optional.empty());

        given(submissionJpaRepository.findByMemberIdAndDailyProblemIdAndStatus(
                member2.getId(),
                dailyProblem2.getId(),
                EntityStatus.ACTIVE
        )).willReturn(Optional.empty());

        given(submissionJpaRepository.save(any()))
                .willReturn(submission1)
                .willReturn(submission2);

        // when
        SubmissionResult result1 = submissionManager.submit(dailyProblem1, userAnswer1);
        SubmissionResult result2 = submissionManager.submit(dailyProblem2, userAnswer2);

        // then
        assertThat(result1.isFirstSubmission()).isTrue();
        assertThat(result2.isFirstSubmission()).isTrue();
        assertThat(dailyProblem1.isSolved()).isTrue();
        assertThat(dailyProblem2.isSolved()).isTrue();

        verify(submissionJpaRepository, times(2)).save(any());
    }

    private Member createMemberWithId(Long id) {
        var member = MemberFixture.createMemberWithOutId(Role.ROLE_MEMBER);
        org.springframework.test.util.ReflectionTestUtils.setField(member, "id", id);
        return member;
    }

}

