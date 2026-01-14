package org.kwakmunsu.haruhana.domain.member.service;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kwakmunsu.haruhana.IntegrationTestSupport;
import org.kwakmunsu.haruhana.domain.category.CategoryFactory;
import org.kwakmunsu.haruhana.domain.category.entity.CategoryTopic;
import org.kwakmunsu.haruhana.domain.category.repository.CategoryTopicJpaRepository;
import org.kwakmunsu.haruhana.domain.member.MemberFixture;
import org.kwakmunsu.haruhana.domain.member.entity.Member;
import org.kwakmunsu.haruhana.domain.member.entity.MemberPreference;
import org.kwakmunsu.haruhana.domain.member.enums.Role;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
class MemberManagerIntegrationTest extends IntegrationTestSupport {

    final MemberManager memberManager;
    final PasswordEncoder passwordEncoder;
    final CategoryFactory categoryFactory;
    final EntityManager entityManager;
    final CategoryTopicJpaRepository categoryTopicJpaRepository;

    CategoryTopic categoryTopic;

    @BeforeEach
    void setUpCategories() {
            categoryFactory.deleteAll();
            categoryFactory.saveAll();

        categoryTopic = categoryTopicJpaRepository.findByName("Java")
                .orElseThrow(() -> new RuntimeException("Java 토픽이 존재하지 않습니다"));
    }

    @Test
    void 회원을_생성한다() {
        // given
        var newProfile = MemberFixture.createNewProfile();

        // when
        var member = memberManager.create(newProfile);

        // then
        assertThat(member).isNotNull()
                .extracting(
                        Member::getLoginId,
                        Member::getNickname,
                        Member::getRole
                )
                .containsExactly(
                        newProfile.loginId(),
                        newProfile.nickname(),
                        Role.ROLE_GUEST
                );
        assertThat(passwordEncoder.matches(newProfile.password(), member.getPassword())).isTrue();
    }

    @Test
    void 회원의_학습_정보를_등록한다() {
        // given
        var newProfile = MemberFixture.createNewProfile();
        var guest = memberManager.create(newProfile);
        var newPreference = MemberFixture.createNewPreference(categoryTopic.getId());

        // when
        var memberPreference = memberManager.registerPreference(guest, newPreference);
        entityManager.flush();

        // then
        assertThat(memberPreference).isNotNull()
                .extracting(
                        MemberPreference::getDifficulty,
                        MemberPreference::getEffectiveAt
                )
                .containsExactly(
                        newPreference.difficulty(),
                        LocalDate.now()
                );

        assertThat(guest.getRole()).isEqualTo(Role.ROLE_MEMBER);
        assertThat(memberPreference.getCategoryTopic().getId()).isEqualTo(newPreference.categoryTopicId());
    }

}