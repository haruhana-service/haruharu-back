package org.kwakmunsu.haruhana.admin.member.service;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.kwakmunsu.haruhana.IntegrationTestSupport;
import org.kwakmunsu.haruhana.domain.member.MemberFixture;
import org.kwakmunsu.haruhana.domain.member.enums.Role;
import org.kwakmunsu.haruhana.domain.member.repository.MemberJpaRepository;
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

}