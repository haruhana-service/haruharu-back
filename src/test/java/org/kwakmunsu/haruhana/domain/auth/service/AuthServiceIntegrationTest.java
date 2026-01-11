package org.kwakmunsu.haruhana.domain.auth.service;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.kwakmunsu.haruhana.IntegrationTestSupport;
import org.kwakmunsu.haruhana.domain.member.MemberFixture;
import org.kwakmunsu.haruhana.domain.member.service.MemberManager;
import org.kwakmunsu.haruhana.domain.member.service.MemberReader;
import org.kwakmunsu.haruhana.global.security.jwt.dto.TokenResponse;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
class AuthServiceIntegrationTest extends IntegrationTestSupport {

    final AuthService authService;
    final MemberReader memberReader;
    final MemberManager memberManager;

    @Test
    void 로그인에_성공한다() {
        // given
        var newProfile = MemberFixture.createNewProfile();
        var member = memberManager.create(newProfile);
        assertThat(member.getRefreshToken()).isNull();

        // when
        var tokenResponse = authService.login(member.getLoginId(), newProfile.password());

        // then
        assertThat(tokenResponse).extracting(
                TokenResponse::accessToken,
                TokenResponse::refreshToken
        ).doesNotContainNull();

        var existingMember = memberReader.findByAccount(member.getLoginId(), newProfile.password());
        assertThat(existingMember.getRefreshToken()).isNotNull();
    }

}