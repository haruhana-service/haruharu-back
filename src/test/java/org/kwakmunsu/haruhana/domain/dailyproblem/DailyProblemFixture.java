package org.kwakmunsu.haruhana.domain.dailyproblem;

import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.kwakmunsu.haruhana.domain.dailyproblem.entity.DailyProblem;
import org.kwakmunsu.haruhana.domain.member.entity.Member;
import org.kwakmunsu.haruhana.domain.problem.entity.Problem;
import org.springframework.test.util.ReflectionTestUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DailyProblemFixture {

    public static DailyProblem createDailyProblem(Member member, Problem problem) {
        DailyProblem dailyProblem = DailyProblem.create(member, problem, LocalDate.now());
        ReflectionTestUtils.setField(dailyProblem, "id", 1L);
        return dailyProblem;
    }

    public static DailyProblem createDailyProblem(Long id, Member member, Problem problem) {
        DailyProblem dailyProblem = DailyProblem.create(member, problem, LocalDate.now());
        ReflectionTestUtils.setField(dailyProblem, "id", id);
        return dailyProblem;
    }

    public static DailyProblem createDailyProblem(Long id, Member member, Problem problem, boolean isSolved) {
        DailyProblem dailyProblem = DailyProblem.create(member, problem, LocalDate.now());
        ReflectionTestUtils.setField(dailyProblem, "id", id);

        if (isSolved) {
            dailyProblem.markAsSolved();
        }

        return dailyProblem;
    }

    public static DailyProblem createSolvedDailyProblem(Long id, Member member, Problem problem) {
        return createDailyProblem(id, member, problem, true);
    }

    public static DailyProblem createUnsolvedDailyProblem(Long id, Member member, Problem problem) {
        return createDailyProblem(id, member, problem, false);
    }

}

