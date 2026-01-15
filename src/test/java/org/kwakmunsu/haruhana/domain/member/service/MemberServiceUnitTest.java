package org.kwakmunsu.haruhana.domain.member.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.kwakmunsu.haruhana.UnitTestSupport;
import org.kwakmunsu.haruhana.domain.category.entity.CategoryTopic;
import org.kwakmunsu.haruhana.domain.member.MemberFixture;
import org.kwakmunsu.haruhana.domain.member.entity.MemberPreference;
import org.kwakmunsu.haruhana.domain.member.enums.Role;
import org.kwakmunsu.haruhana.domain.problem.enums.ProblemDifficulty;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class MemberServiceUnitTest extends UnitTestSupport {

    @Mock
    MemberReader memberReader;

    @InjectMocks
    MemberService memberService;

    @Test
    void 회원_프로필을_조회한다() {
        // given
        var member = MemberFixture.createMember(Role.ROLE_MEMBER);
        var memberPreference = MemberPreference.create(member, CategoryTopic.create(1L, "알고리즘"), ProblemDifficulty.EASY, LocalDate.now());

        given(memberReader.getMemberPreference(member.getId())).willReturn(memberPreference);

        // when
        var memberProfileResponse = memberService.getProfile(member.getId());

        // then
        assertThat(memberProfileResponse).isNotNull()
                .extracting(
                        MemberProfileResponse::loginId,
                        MemberProfileResponse::nickname,
                        MemberProfileResponse::categoryTopicName,
                        MemberProfileResponse::difficulty
                ).containsExactly(
                        member.getLoginId(),
                        member.getNickname(),
                        memberPreference.getCategoryTopic().getName(),
                        memberPreference.getDifficulty().name()
                );
    }

}