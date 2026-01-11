package org.kwakmunsu.haruhana.domain.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.kwakmunsu.haruhana.UnitTestSupport;
import org.kwakmunsu.haruhana.domain.member.MemberFixture;
import org.kwakmunsu.haruhana.domain.member.enums.Role;
import org.kwakmunsu.haruhana.global.support.error.ErrorType;
import org.kwakmunsu.haruhana.global.support.error.HaruHanaException;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class MemberServiceUnitTest extends UnitTestSupport {

    @Mock
    MemberManager memberManager;

    @Mock
    MemberReader memberReader;

    @Mock
    MemberValidator memberValidator;

    @InjectMocks
    MemberService memberService;

    @Test
    void 회원_생성을_성공한다() {
        // given
        var newProfile = MemberFixture.createNewProfile();
        var member = MemberFixture.createMember(Role.ROLE_GUEST);

        given(memberManager.create(any())).willReturn(member);

        // when
        Long memberId = memberService.createMember(newProfile);

        // then
        assertThat(memberId).isEqualTo(member.getId());
    }

    @Test
    void 회원_생성을_실패한다() {
        // given
        var newProfile = MemberFixture.createNewProfile();

        willThrow(new HaruHanaException(ErrorType.DUPLICATE_NICKNAME))
                .given(memberValidator).validateNew(any());

        // when & then
        assertThatThrownBy(() -> memberService.createMember(newProfile))
                .isInstanceOf(HaruHanaException.class)
                .hasMessage(ErrorType.DUPLICATE_NICKNAME.getMessage());
    }

    @Test
    void 회원_학습_정보를_등록한다() {
        // given
        var member = MemberFixture.createMember(Role.ROLE_GUEST);

        given(memberReader.find(any())).willReturn(member);

        // when
        memberService.registerPreference(
                MemberFixture.createNewPreference(1L),
                member.getId()
        );

        // then
        verify(memberValidator, times(1)).validateGuest(any());
        verify(memberManager, times(1)).registerPreference(any(), any());
    }

    @Test
    void GUEST_회원이_아닐_경우_학습_정보를_등록에_실패한다() {
        // given
        var member = MemberFixture.createMember(Role.ROLE_MEMBER);

        given(memberReader.find(any())).willReturn(member);
        willThrow(new HaruHanaException(ErrorType.FORBIDDEN_ERROR))
                .given(memberValidator).validateGuest(any());

        // when & then
        assertThatThrownBy(() -> memberService.registerPreference(MemberFixture.createNewPreference(1L), member.getId()))
                .isInstanceOf(HaruHanaException.class)
                .hasMessage(ErrorType.FORBIDDEN_ERROR.getMessage());
    }

    @Test
    void 존재하지_않는_카테고리로_등록_시_학습_정보_등록에_실패한다() {
        // given
        var member = MemberFixture.createMember(Role.ROLE_MEMBER);

        given(memberReader.find(any())).willReturn(member);
        willThrow(new HaruHanaException(ErrorType.NOT_FOUND_CATEGORY))
                .given(memberManager).registerPreference(any(), any());

        // when & then
        assertThatThrownBy(() -> memberService.registerPreference(MemberFixture.createNewPreference(1L), member.getId()))
                .isInstanceOf(HaruHanaException.class)
                .hasMessage(ErrorType.NOT_FOUND_CATEGORY.getMessage());

        verify(memberValidator, times(1)).validateGuest(any());
    }

}