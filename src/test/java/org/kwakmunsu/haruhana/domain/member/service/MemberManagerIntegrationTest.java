package org.kwakmunsu.haruhana.domain.member.service;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.kwakmunsu.haruhana.IntegrationTestSupport;
import org.kwakmunsu.haruhana.domain.member.entity.Member;
import org.kwakmunsu.haruhana.domain.member.enums.Role;
import org.kwakmunsu.haruhana.domain.member.service.dto.request.NewProfile;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
class MemberManagerIntegrationTest extends IntegrationTestSupport {

    final MemberManager memberManager;

    @Test
    void 회원을_생성한다() {
        // given
        var newProfile = new NewProfile(
                "newLoginId",
                "newPassword",
                "newNickname"
        );

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
    }

}