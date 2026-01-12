package org.kwakmunsu.haruhana.domain.problem;

import java.time.LocalDate;
import org.kwakmunsu.haruhana.domain.category.entity.CategoryTopic;
import org.kwakmunsu.haruhana.domain.problem.entity.Problem;
import org.kwakmunsu.haruhana.domain.problem.enums.ProblemDifficulty;
import org.kwakmunsu.haruhana.domain.problem.service.Prompt;

public class ProblemFixture {

    public static Problem creatProblem() {
        return Problem.create(
                "문제 제목",
                "문제 내용",
                "문제 답안",
                CategoryTopic.create(1L, "카테고리 토픽 이름"),
                ProblemDifficulty.MEDIUM,
                LocalDate.now(),
                Prompt.V1_PROMPT.name()
        );
    }

}