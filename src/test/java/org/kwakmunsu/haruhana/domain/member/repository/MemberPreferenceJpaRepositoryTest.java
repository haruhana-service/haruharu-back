package org.kwakmunsu.haruhana.domain.member.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kwakmunsu.haruhana.IntegrationTestSupport;
import org.kwakmunsu.haruhana.domain.category.CategoryFactory;
import org.kwakmunsu.haruhana.domain.category.entity.CategoryTopic;
import org.kwakmunsu.haruhana.domain.category.repository.CategoryTopicJpaRepository;
import org.kwakmunsu.haruhana.domain.member.MemberFixture;
import org.kwakmunsu.haruhana.domain.member.entity.MemberPreference;
import org.kwakmunsu.haruhana.domain.member.enums.Role;
import org.kwakmunsu.haruhana.domain.problem.enums.ProblemDifficulty;
import org.kwakmunsu.haruhana.global.entity.EntityStatus;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
class MemberPreferenceJpaRepositoryTest extends IntegrationTestSupport{

    final MemberPreferenceJpaRepository memberPreferenceJpaRepository;
    final MemberJpaRepository memberJpaRepository;
    final CategoryFactory categoryFactory;
    final CategoryTopicJpaRepository categoryTopicJpaRepository;
    CategoryTopic categoryTopic;

    @BeforeEach
    void setUp() {
        categoryFactory.deleteAll();
        categoryFactory.saveAll();

        categoryTopic = categoryTopicJpaRepository.findByName("Java").orElseThrow();
    }

    @Test
    void findByMemberIdWithMember_JOIN_FETCH_확인() {
        // given
        var member = memberJpaRepository.save( MemberFixture.createMemberWithOutId(Role.ROLE_MEMBER));
        var memberPreference = MemberPreference.create(member, categoryTopic, ProblemDifficulty.MEDIUM, LocalDate.now());
        memberPreferenceJpaRepository.save(memberPreference);

        // when
        var foundMemberPreference = memberPreferenceJpaRepository.findByMemberIdWithMember(member.getId(), EntityStatus.ACTIVE)
                .orElseThrow();

        // then
        assertThat(foundMemberPreference.getMember()).isEqualTo(member);
    }

}