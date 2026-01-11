package org.kwakmunsu.haruhana.domain.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.kwakmunsu.haruhana.UnitTestSupport;
import org.kwakmunsu.haruhana.domain.member.MemberFixture;
import org.kwakmunsu.haruhana.domain.member.entity.Member;
import org.kwakmunsu.haruhana.domain.member.enums.Role;
import org.kwakmunsu.haruhana.domain.member.repository.MemberJpaRepository;
import org.kwakmunsu.haruhana.global.support.error.ErrorType;
import org.kwakmunsu.haruhana.global.support.error.HaruHanaException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;

class MemberReaderUnitTest extends UnitTestSupport {

    @Mock
    MemberJpaRepository memberJpaRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    MemberReader memberReader;

    @Test
    void 유효한_계정으로_회원정보_조회에_성공한다() {
        // given
        var createMember = MemberFixture.createMember(Role.ROLE_GUEST);
        var loginId = createMember.getLoginId();
        var password = createMember.getPassword();

        given(memberJpaRepository.findByLoginIdAndStatus(any(), any()))
                .willReturn(Optional.of(createMember));
        given(passwordEncoder.matches(any(), any())).willReturn(true);

        // when
        var existingMember = memberReader.findByAccount(loginId, password);

        // then
        assertThat(existingMember).extracting(
                Member::getLoginId,
                Member::getRole
        ).containsExactly(
                loginId,
                Role.ROLE_GUEST
        );
    }

    @Test
    void 존재하지않는_LoginId_일_경우_회원정보_조회에_실패한다() {
        // given
        var createMember = MemberFixture.createMember(Role.ROLE_GUEST);
        var loginId = createMember.getLoginId();
        var password = createMember.getPassword();

        given(memberJpaRepository.findByLoginIdAndStatus(any(), any()))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> memberReader.findByAccount(loginId, password))
                .isInstanceOf(HaruHanaException.class)
                .hasMessage(ErrorType.INVALID_ACCOUNT.getMessage());
    }

    @Test
    void 비밀번호가_불일치할_경우_회원정보_조회에_실패한다() {
        // given
        var createMember = MemberFixture.createMember(Role.ROLE_GUEST);
        var loginId = createMember.getLoginId();
        var password = createMember.getPassword();

        given(memberJpaRepository.findByLoginIdAndStatus(any(), any()))
                .willReturn(Optional.of(createMember));
        given(passwordEncoder.matches(any(), any())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> memberReader.findByAccount(loginId, password))
                .isInstanceOf(HaruHanaException.class)
                .hasMessage(ErrorType.INVALID_ACCOUNT.getMessage());
    }

}