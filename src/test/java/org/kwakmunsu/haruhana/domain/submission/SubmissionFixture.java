package org.kwakmunsu.haruhana.domain.submission;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.kwakmunsu.haruhana.domain.dailyproblem.entity.DailyProblem;
import org.kwakmunsu.haruhana.domain.member.entity.Member;
import org.kwakmunsu.haruhana.domain.submission.entity.Submission;
import org.springframework.test.util.ReflectionTestUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SubmissionFixture {

    public static final String USER_ANSWER = "사용자 답변";

    public static Submission createSubmission(Member member, DailyProblem dailyProblem) {
        Submission submission = Submission.create(member, dailyProblem, USER_ANSWER, LocalDateTime.now());
        ReflectionTestUtils.setField(submission, "id", 1L);
        return submission;
    }

    public static Submission createSubmission(Member member, DailyProblem dailyProblem, String answer) {
        Submission submission = Submission.create(member, dailyProblem, answer, LocalDateTime.now());
        ReflectionTestUtils.setField(submission, "id", 1L);
        return submission;
    }

    public static Submission createSubmission(Member member, DailyProblem dailyProblem, String answer, LocalDateTime now) {
        Submission submission = Submission.create(member, dailyProblem, answer, now);
        ReflectionTestUtils.setField(submission, "id", 1L);
        return submission;
    }

    public static Submission createSubmission(Long id, Member member, DailyProblem dailyProblem, String answer) {
        Submission submission = Submission.create(member, dailyProblem, answer, LocalDateTime.now());
        ReflectionTestUtils.setField(submission, "id", id);
        return submission;
    }

}

