package org.kwakmunsu.haruhana.admin.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.kwakmunsu.haruhana.UnitTestSupport;
import org.kwakmunsu.haruhana.admin.member.enums.SortBy;
import org.kwakmunsu.haruhana.admin.member.service.dto.AdminMemberPreviewResponse;
import org.kwakmunsu.haruhana.domain.member.MemberFixture;
import org.kwakmunsu.haruhana.domain.member.entity.Member;
import org.kwakmunsu.haruhana.domain.member.enums.Role;
import org.kwakmunsu.haruhana.domain.member.repository.MemberQueryDslRepository;
import org.kwakmunsu.haruhana.global.entity.EntityStatus;
import org.kwakmunsu.haruhana.global.support.OffsetLimit;
import org.kwakmunsu.haruhana.global.support.response.PageResponse;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class AdminMemberReaderUnitTest extends UnitTestSupport {

    @Mock
    MemberQueryDslRepository memberQueryDslRepository;

    @InjectMocks
    AdminMemberReader adminMemberReader;

    @Test
    void 다음_페이지가_존재하는_경우로_회원_정보를_조회한다() {
        // given
        List<Member> members = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            members.add(MemberFixture.createMember(
                    "user" + i,
                    "유저" + i
            ));
        }

        given(memberQueryDslRepository.findMembers(any(), any(), any())).willReturn(members);

        // when
        PageResponse<AdminMemberPreviewResponse> response = adminMemberReader.findMembers(
                null,
                SortBy.JOIN_DESC,
                new OffsetLimit(1, 2)
        );

        // then
        assertThat(response.hasNext()).isTrue();
        assertThat(response.contents()).hasSize(2);
    }

    @Test
    void 미지막_페이지의_회원_정보를_조회한다() {
        // given
        List<Member> members = List.of(
                MemberFixture.createMember("user2", "유저2"),
                MemberFixture.createMember("user3", "유저3"),
                MemberFixture.createMember("user1", "유저1")
        );
        given(memberQueryDslRepository.findMembers(any(), any(), any())).willReturn(members);

        // when
        PageResponse<AdminMemberPreviewResponse> response = adminMemberReader.findMembers(
                null,
                SortBy.JOIN_DESC,
                new OffsetLimit(1, 20)
        );

        // then
        assertThat(response.hasNext()).isFalse();
        assertThat(response.contents()).extracting(
                AdminMemberPreviewResponse::loginId,
                AdminMemberPreviewResponse::nickname
        ).containsExactly(
                tuple("user2", "유저2"),
                tuple("user3", "유저3"),
                tuple("user1", "유저1")
        );
    }

    @Test
    void 조회된_회원_정보를_확인한다() {
        // given
        List<Member> members = List.of(MemberFixture.createMember("user2", "유저2"));
        given(memberQueryDslRepository.findMembers(any(), any(), any())).willReturn(members);

        // when
        PageResponse<AdminMemberPreviewResponse> response = adminMemberReader.findMembers(
                null,
                SortBy.JOIN_DESC,
                new OffsetLimit(1, 20)
        );

        // then
        assertThat(response.hasNext()).isFalse();
        assertThat(response.contents()).extracting(
                AdminMemberPreviewResponse::loginId,
                AdminMemberPreviewResponse::nickname,
                AdminMemberPreviewResponse::role,
                AdminMemberPreviewResponse::status
        ).containsExactly(
                tuple("user2", "유저2", Role.ROLE_MEMBER, EntityStatus.ACTIVE)
        );
    }

}