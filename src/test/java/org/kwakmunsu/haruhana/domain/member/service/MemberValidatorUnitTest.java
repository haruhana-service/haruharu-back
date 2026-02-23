package org.kwakmunsu.haruhana.domain.member.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;

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

    @Mock
    NicknameFilter nicknameFilter;

    @InjectMocks
    MemberValidator memberValidator;

    @Test
    void 유효한_회원정보로_생성_시_도메인_규칙_검증을_통과한다() {
        // given
        given(memberJpaRepository.existsByLoginIdAndStatus(any(), any())).willReturn(false);
        given(memberJpaRepository.existsByNicknameAndStatus(any(), any())).willReturn(false);

        var newProfile = MemberFixture.createNewProfile();

        // when & then
        memberValidator.validateNew(newProfile);
    }

    @Test
    void 중복된_로그인아이디로_생성_시_도메인_규칙_검증에_걸린다() {
        // given
        given(memberJpaRepository.existsByLoginIdAndStatus(any(), any())).willReturn(true);

        var newProfile = MemberFixture.createNewProfile();

        // when & then
        assertThatThrownBy(() -> memberValidator.validateNew(newProfile))
                .isInstanceOf(HaruHanaException.class)
                .hasMessage(ErrorType.DUPLICATE_LOGIN_ID.getMessage());
    }

    @Test
    void 중복된_닉네임으로_생성_시_도메인_규칙_검증에_걸린다() {
        // given
        given(memberJpaRepository.existsByLoginIdAndStatus(any(), any())).willReturn(false);
        given(memberJpaRepository.existsByNicknameAndStatus(any(), any())).willReturn(true);

        var newProfile = MemberFixture.createNewProfile();

        // when & then
        assertThatThrownBy(() -> memberValidator.validateNew(newProfile))
                .isInstanceOf(HaruHanaException.class)
                .hasMessage(ErrorType.DUPLICATE_NICKNAME.getMessage());
    }

    @Test
    void 부적절한_단어가_포함된_닉네임으로_생성_시_도메인_규칙_검증에_걸린다() {
        // given
        given(memberJpaRepository.existsByLoginIdAndStatus(any(), any())).willReturn(false);
        doThrow(new HaruHanaException(ErrorType.INVALID_NICKNAME)).when(nicknameFilter).validate(any());

        var newProfile = MemberFixture.createNewProfile();

        // when & then
        assertThatThrownBy(() -> memberValidator.validateNew(newProfile))
                .isInstanceOf(HaruHanaException.class)
                .hasMessage(ErrorType.INVALID_NICKNAME.getMessage());
    }

    @Test
    void 사용_가능한_닉네임_검사_시_통과한다() {
        // given
        given(memberJpaRepository.existsByNicknameAndStatus(any(), any())).willReturn(false);

        // when & then
        memberValidator.validateNicknameAvailable("정상닉네임");
    }

    @Test
    void 중복된_닉네임_검사_시_예외가_발생한다() {
        // given
        given(memberJpaRepository.existsByNicknameAndStatus(any(), any())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> memberValidator.validateNicknameAvailable("중복닉네임"))
                .isInstanceOf(HaruHanaException.class)
                .hasMessage(ErrorType.DUPLICATE_NICKNAME.getMessage());
    }

    @Test
    void 부적절한_단어가_포함된_닉네임_검사_시_예외가_발생한다() {
        // given
        doThrow(new HaruHanaException(ErrorType.INVALID_NICKNAME)).when(nicknameFilter).validate(any());

        // when & then
        assertThatThrownBy(() -> memberValidator.validateNicknameAvailable("욕설포함닉네임"))
                .isInstanceOf(HaruHanaException.class)
                .hasMessage(ErrorType.INVALID_NICKNAME.getMessage());
    }

}
