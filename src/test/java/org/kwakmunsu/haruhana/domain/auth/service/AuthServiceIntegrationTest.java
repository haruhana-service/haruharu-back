package org.kwakmunsu.haruhana.domain.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.kwakmunsu.haruhana.IntegrationTestSupport;
import org.kwakmunsu.haruhana.domain.member.MemberFixture;
import org.kwakmunsu.haruhana.domain.member.repository.MemberJpaRepository;
import org.kwakmunsu.haruhana.domain.member.service.MemberManager;
import org.kwakmunsu.haruhana.domain.member.service.MemberReader;
import org.kwakmunsu.haruhana.global.security.jwt.dto.TokenResponse;
import org.kwakmunsu.haruhana.global.support.error.ErrorType;
import org.kwakmunsu.haruhana.global.support.error.HaruHanaException;

@RequiredArgsConstructor
class AuthServiceIntegrationTest extends IntegrationTestSupport {

    final AuthService authService;
    final MemberReader memberReader;
    final MemberManager memberManager;
    final MemberJpaRepository memberJpaRepository;

    @AfterEach
    void tearDown() {
        memberJpaRepository.deleteAll();
    }

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
        var tokenResponse = authService.reissue(oldRefreshToken, member.getId());

        // then
        assertThat(tokenResponse).extracting(
                TokenResponse::accessToken,
                TokenResponse::refreshToken
        ).doesNotContainNull();

        var existingMember = memberReader.findByAccount(member.getLoginId(), newProfile.password());
        boolean isEquals = existingMember.isEqualsRefreshToken(tokenResponse.refreshToken());
        assertThat(isEquals).isTrue();
    }

    @Test
    void 회원이_가지고있는_RT와_다른_RT로_재발급_요청이_온다면_탈취_감지로_토큰_무효화_후_예외를_반환한다() {
        // given
        var newProfile = MemberFixture.createNewProfile();
        var member = memberManager.create(newProfile);
        authService.login(member.getLoginId(), newProfile.password());

        var invalidRefreshToken = "invalidToken";

        // when & then
        assertThatThrownBy(() -> authService.reissue(invalidRefreshToken, member.getId()))
                .isInstanceOf(HaruHanaException.class)
                .hasMessage(ErrorType.TOKEN_THEFT_DETECTED.getMessage());

        var existingMember = memberReader.findByAccount(member.getLoginId(), newProfile.password());

        assertThat(existingMember.getRefreshToken()).isNull();
    }

}