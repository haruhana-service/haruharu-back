package org.kwakmunsu.haruhana.admin.problem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kwakmunsu.haruhana.IntegrationTestSupport;
import org.kwakmunsu.haruhana.domain.category.CategoryFactory;
import org.kwakmunsu.haruhana.domain.category.entity.CategoryTopic;
import org.kwakmunsu.haruhana.domain.category.repository.CategoryTopicJpaRepository;
import org.kwakmunsu.haruhana.domain.problem.entity.Problem;
import org.kwakmunsu.haruhana.domain.problem.enums.ProblemDifficulty;
import org.kwakmunsu.haruhana.domain.problem.repository.ProblemJpaRepository;
import org.kwakmunsu.haruhana.global.support.OffsetLimit;
import org.kwakmunsu.haruhana.global.support.response.PageResponse;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
class AdminProblemReaderIntegrationTest extends IntegrationTestSupport {

    final AdminProblemReader adminProblemReader;
    final ProblemJpaRepository problemJpaRepository;
    final CategoryTopicJpaRepository categoryTopicJpaRepository;
    final CategoryFactory categoryFactory;
    final EntityManager entityManager;

    @BeforeEach
    void setUp() {
        categoryFactory.deleteAll();
        categoryFactory.saveAll();
        entityManager.flush();
    }

    @Test
    void 날짜를_지정하면_해당_날짜의_문제_목록을_조회한다() {
        // given
        LocalDate targetDate = LocalDate.of(2025, 1, 1);
        CategoryTopic topic = categoryTopicJpaRepository.findByName("Java").orElseThrow();

        problemJpaRepository.save(createProblem("문제1", topic, ProblemDifficulty.EASY, targetDate));
        problemJpaRepository.save(createProblem("문제2", topic, ProblemDifficulty.MEDIUM, targetDate));
        entityManager.flush();
        entityManager.clear();

        // when
        PageResponse<AdminProblemPreviewResponse> response = adminProblemReader.findProblems(
                targetDate,
                new OffsetLimit(1, 20)
        );

        // then
        assertThat(response.hasNext()).isFalse();
        assertThat(response.contents()).hasSize(2);
    }

    @Test
    void 날짜가_null이면_오늘_날짜의_문제_목록을_조회한다() {
        // given
        CategoryTopic topic = categoryTopicJpaRepository.findByName("Java").orElseThrow();

        problemJpaRepository.save(createProblem("오늘의 문제", topic, ProblemDifficulty.EASY, LocalDate.now()));
        entityManager.flush();
        entityManager.clear();

        // when
        PageResponse<AdminProblemPreviewResponse> response = adminProblemReader.findProblems(
                null,
                new OffsetLimit(1, 20)
        );

        // then
        assertThat(response.contents()).hasSize(1);
    }

    @Test
    void 다음_페이지가_존재하는_경우_hasNext가_true이다() {
        // given
        LocalDate targetDate = LocalDate.now();
        CategoryTopic topic = categoryTopicJpaRepository.findByName("Java").orElseThrow();

        for (int i = 0; i < 3; i++) {
            problemJpaRepository.save(createProblem("문제" + i, topic, ProblemDifficulty.EASY, targetDate));
        }
        entityManager.flush();
        entityManager.clear();

        // when
        PageResponse<AdminProblemPreviewResponse> response = adminProblemReader.findProblems(
                targetDate,
                new OffsetLimit(1, 2)
        );

        // then
        assertThat(response.hasNext()).isTrue();
        assertThat(response.contents()).hasSize(2);
    }

    @Test
    void 마지막_페이지이면_hasNext가_false이다() {
        // given
        LocalDate targetDate = LocalDate.now();
        CategoryTopic topic = categoryTopicJpaRepository.findByName("Java").orElseThrow();

        for (int i = 0; i < 2; i++) {
            problemJpaRepository.save(createProblem("문제" + i, topic, ProblemDifficulty.EASY, targetDate));
        }
        entityManager.flush();
        entityManager.clear();

        // when
        PageResponse<AdminProblemPreviewResponse> response = adminProblemReader.findProblems(
                targetDate,
                new OffsetLimit(1, 20)
        );

        // then
        assertThat(response.hasNext()).isFalse();
        assertThat(response.contents()).hasSize(2);
    }

    @Test
    void 조회된_문제의_응답_필드를_검증한다() {
        // given
        LocalDate targetDate = LocalDate.now();
        CategoryTopic topic = categoryTopicJpaRepository.findByName("Java").orElseThrow();

        problemJpaRepository.save(createProblem("알고리즘 문제", topic, ProblemDifficulty.EASY, targetDate));
        entityManager.flush();
        entityManager.clear();

        // when
        PageResponse<AdminProblemPreviewResponse> response = adminProblemReader.findProblems(
                targetDate,
                new OffsetLimit(1, 20)
        );

        // then
        assertThat(response.contents())
                .extracting(
                        AdminProblemPreviewResponse::title,
                        AdminProblemPreviewResponse::description,
                        AdminProblemPreviewResponse::aiAnswer,
                        AdminProblemPreviewResponse::categoryTopic,
                        AdminProblemPreviewResponse::difficulty
                ).containsExactly(
                        tuple("알고리즘 문제", "테스트 설명입니다.", "테스트 AI 답변입니다.", "Java", ProblemDifficulty.EASY)
                );
    }

    @Test
    void 다른_날짜의_문제는_조회되지_않는다() {
        // given
        CategoryTopic topic = categoryTopicJpaRepository.findByName("Java").orElseThrow();
        LocalDate targetDate = LocalDate.of(2025, 1, 1);
        LocalDate otherDate = LocalDate.of(2025, 1, 2);

        problemJpaRepository.save(createProblem("조회 대상 문제", topic, ProblemDifficulty.EASY, targetDate));
        problemJpaRepository.save(createProblem("다른 날짜 문제", topic, ProblemDifficulty.EASY, otherDate));
        entityManager.flush();
        entityManager.clear();

        // when
        PageResponse<AdminProblemPreviewResponse> response = adminProblemReader.findProblems(
                targetDate,
                new OffsetLimit(1, 20)
        );

        // then
        assertThat(response.contents()).hasSize(1);
        assertThat(response.contents().getFirst().title()).isEqualTo("조회 대상 문제");
    }

    @Test
    void 조회된_문제가_없으면_빈_목록을_반환한다() {
        // given
        LocalDate targetDate = LocalDate.of(2099, 1, 1);

        // when
        PageResponse<AdminProblemPreviewResponse> response = adminProblemReader.findProblems(
                targetDate,
                new OffsetLimit(1, 20)
        );

        // then
        assertThat(response.hasNext()).isFalse();
        assertThat(response.contents()).isEmpty();
    }

    private Problem createProblem(String title, CategoryTopic topic, ProblemDifficulty difficulty, LocalDate date) {
        return Problem.create(
                title,
                "테스트 설명입니다.",
                "테스트 AI 답변입니다.",
                topic,
                difficulty,
                date,
                "V1_PROMPT"
        );
    }

}