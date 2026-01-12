package org.kwakmunsu.haruhana.domain.problem;

import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.kwakmunsu.haruhana.domain.category.entity.CategoryTopic;
import org.kwakmunsu.haruhana.domain.problem.entity.Problem;
import org.kwakmunsu.haruhana.domain.problem.enums.ProblemDifficulty;
import org.springframework.test.util.ReflectionTestUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProblemFixture {

    public static final String TITLE = "테스트 문제";
    public static final String DESCRIPTION = "테스트 설명";
    public static final String AI_ANSWER = "AI 답변";
    public static final ProblemDifficulty DIFFICULTY = ProblemDifficulty.MEDIUM;
    public static final String PROMPT_VERSION = "V1_PROMPT";

    public static Problem createProblem(CategoryTopic categoryTopic) {
        Problem problem = Problem.create(
                TITLE,
                DESCRIPTION,
                AI_ANSWER,
                categoryTopic,
                DIFFICULTY,
                LocalDate.now(),
                PROMPT_VERSION
        );
        ReflectionTestUtils.setField(problem, "id", 1L);
        return problem;
    }

    public static Problem createProblem(Long id, CategoryTopic categoryTopic) {
        Problem problem = Problem.create(
                TITLE,
                DESCRIPTION,
                AI_ANSWER,
                categoryTopic,
                DIFFICULTY,
                LocalDate.now(),
                PROMPT_VERSION
        );
        ReflectionTestUtils.setField(problem, "id", id);
        return problem;
    }

    public static Problem createProblem(
            Long id,
            String title,
            String description,
            CategoryTopic categoryTopic,
            ProblemDifficulty difficulty
    ) {
        Problem problem = Problem.create(
                title,
                description,
                AI_ANSWER,
                categoryTopic,
                difficulty,
                LocalDate.now(),
                PROMPT_VERSION
        );
        ReflectionTestUtils.setField(problem, "id", id);
        return problem;
    }

}

