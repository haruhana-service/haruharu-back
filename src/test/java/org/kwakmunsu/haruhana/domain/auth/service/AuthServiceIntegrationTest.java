package org.kwakmunsu.haruhana.domain.auth.service;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.kwakmunsu.haruhana.IntegrationTestSupport;
import org.kwakmunsu.haruhana.domain.member.MemberFixture;
import org.kwakmunsu.haruhana.domain.member.service.MemberManager;
import org.kwakmunsu.haruhana.domain.member.service.MemberReader;
import org.kwakmunsu.haruhana.global.security.jwt.TokenHasher;
import org.kwakmunsu.haruhana.global.security.jwt.dto.TokenResponse;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
class AuthServiceIntegrationTest extends IntegrationTestSupport {

    final AuthService authService;
    final MemberReader memberReader;
    final MemberManager memberManager;
    final EntityManager entityManager;

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

    @Test
    void 토큰_재발급에_성공한다() {
        // given
        var newProfile = MemberFixture.createNewProfile();
        var member = memberManager.create(newProfile);
        var oldRefreshToken = authService.login(member.getLoginId(), newProfile.password()).refreshToken();

        // when
        var tokenResponse = authService.reissue(oldRefreshToken);

        // then
        assertThat(tokenResponse).extracting(
                TokenResponse::accessToken,
                TokenResponse::refreshToken
        ).doesNotContainNull();

        var existingMember = memberReader.findByAccount(member.getLoginId(), newProfile.password());
        var isEquals = existingMember.getRefreshToken().equals(TokenHasher.hash(tokenResponse.refreshToken()));
        assertThat(isEquals).isTrue();
    }

    @Test
    void 로그아웃을_한다() {
        // given
        var newProfile = MemberFixture.createNewProfile();
        var member = memberManager.create(newProfile);
        authService.login(member.getLoginId(), newProfile.password());

        entityManager.flush();
        entityManager.clear();

        assertThat(member.getRefreshToken()).isNotNull();

        // when
        authService.logout(member.getId());

        // then
        var existingMember = memberReader.findByAccount(member.getLoginId(), newProfile.password());
        assertThat(existingMember.getRefreshToken()).isNull();
    }

}