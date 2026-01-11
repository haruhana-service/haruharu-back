package org.kwakmunsu.haruhana.domain.member.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.Test;
import org.kwakmunsu.haruhana.UnitTestSupport;
import org.kwakmunsu.haruhana.domain.member.MemberFixture;
import org.kwakmunsu.haruhana.domain.member.repository.MemberJpaRepository;
import org.kwakmunsu.haruhana.global.support.error.ErrorType;
import org.kwakmunsu.haruhana.global.support.error.HaruHanaException;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class MemberValidatorUnitTest extends UnitTestSupport {

    @Mock
    MemberJpaRepository memberJpaRepository;

    @InjectMocks
    MemberValidator memberValidator;

    @Test
    void 유효한_회원정보로_생성_시_도메인_규칙_검증을_통과한다() {
        // given
        given(memberJpaRepository.existsByLoginId(any())).willReturn(false);
        given(memberJpaRepository.existsByNickname(any())).willReturn(false);

        var newProfile = MemberFixture.createNewProfile();

        // when & then
        memberValidator.validateNew(newProfile);
    }

    @Test
    void 중복된_로그인아이디로_생성_시_도메인_규칙_검증에_걸린다() {
        // given
        given(memberJpaRepository.existsByLoginId(any())).willReturn(true);

        var newProfile = MemberFixture.createNewProfile();

        // when & then
        assertThatThrownBy(() -> memberValidator.validateNew(newProfile))
                .isInstanceOf(HaruHanaException.class)
                .hasMessage(ErrorType.DUPLICATE_LOGIN_ID.getMessage());
    }

    @Test
    void 중복된_닉네임으로_생성_시_도메인_규칙_검증에_걸린다() {
        // given
        given(memberJpaRepository.existsByLoginId(any())).willReturn(false);
        given(memberJpaRepository.existsByNickname(any())).willReturn(true);

        var newProfile = MemberFixture.createNewProfile();

        // when & then
        assertThatThrownBy(() -> memberValidator.validateNew(newProfile))
                .isInstanceOf(HaruHanaException.class)
                .hasMessage(ErrorType.DUPLICATE_NICKNAME.getMessage());
    }

}