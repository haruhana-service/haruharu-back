package org.kwakmunsu.haruhana.admin.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.kwakmunsu.haruhana.IntegrationTestSupport;
import org.kwakmunsu.haruhana.domain.member.MemberFixture;
import org.kwakmunsu.haruhana.domain.member.enums.Role;
import org.kwakmunsu.haruhana.domain.member.repository.MemberJpaRepository;
import org.kwakmunsu.haruhana.global.support.error.ErrorType;
import org.kwakmunsu.haruhana.global.support.error.HaruHanaException;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
class AdminMemberManagerIntegrationTest extends IntegrationTestSupport {

    final MemberJpaRepository memberJpaRepository;
    final AdminMemberManager adminMemberManager;
    final EntityManager entityManager;

    @Test
    void 관리자가_회원_권한을_MEMBER에서_ADMIN으로변경한다() {
        // given
        var member = MemberFixture.createMemberWithOutId(Role.ROLE_MEMBER);
        memberJpaRepository.save(member);

        // when
        adminMemberManager.updateMemberRole(member.getId(), Role.ROLE_ADMIN);
        entityManager.flush();

        // then
        var updatedMember = memberJpaRepository.findById(member.getId()).orElseThrow();

        assertThat(updatedMember.getRole()).isEqualTo(Role.ROLE_ADMIN);
    }

    @Test
    void 관리자가_회원_권한을_ADMIN에서_MEMBER으로변경한다() {
        // given
        var admin = MemberFixture.createMemberWithOutId(Role.ROLE_ADMIN);
        memberJpaRepository.save(admin);

        // when
        adminMemberManager.updateMemberRole(admin.getId(), Role.ROLE_MEMBER);
        entityManager.flush();

        // then
        var updatedMember = memberJpaRepository.findById(admin.getId()).orElseThrow();

        assertThat(updatedMember.getRole()).isEqualTo(Role.ROLE_MEMBER);
    }

    @Test
    void 존재하지_않는_회원의_권한을_변경하면_예외를_반환한다() {
        // given
        var nonExistentMemberId = 999999L;

        // when & then
        assertThatThrownBy(() -> adminMemberManager.updateMemberRole(nonExistentMemberId, Role.ROLE_ADMIN))
                .isInstanceOf(HaruHanaException.class)
                .hasMessage(ErrorType.NOT_FOUND_MEMBER.getMessage());
    }

}