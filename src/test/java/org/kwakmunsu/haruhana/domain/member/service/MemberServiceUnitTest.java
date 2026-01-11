package org.kwakmunsu.haruhana.domain.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;

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

}