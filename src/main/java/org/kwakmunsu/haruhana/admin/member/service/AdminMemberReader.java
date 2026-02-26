package org.kwakmunsu.haruhana.admin.member.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.kwakmunsu.haruhana.admin.member.enums.SortBy;
import org.kwakmunsu.haruhana.admin.member.service.dto.AdminMemberPreferenceResponse;
import org.kwakmunsu.haruhana.admin.member.service.dto.AdminMemberPreviewResponse;
import org.kwakmunsu.haruhana.domain.member.entity.Member;
import org.kwakmunsu.haruhana.domain.member.entity.MemberPreference;
import org.kwakmunsu.haruhana.domain.member.repository.MemberPreferenceJpaRepository;
import org.kwakmunsu.haruhana.domain.member.repository.MemberQueryDslRepository;
import org.kwakmunsu.haruhana.global.entity.EntityStatus;
import org.kwakmunsu.haruhana.global.support.OffsetLimit;
import org.kwakmunsu.haruhana.global.support.error.ErrorType;
import org.kwakmunsu.haruhana.global.support.error.HaruHanaException;
import org.kwakmunsu.haruhana.global.support.response.PageResponse;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AdminMemberReader {

    private final MemberQueryDslRepository memberQueryDslRepository;
    private final MemberPreferenceJpaRepository memberPreferenceJpaRepository;

    public PageResponse<AdminMemberPreviewResponse> findMembers(String search, SortBy sortBy, OffsetLimit offsetLimit) {
        List<Member> members = memberQueryDslRepository.findMembers(search, sortBy, offsetLimit);

        boolean hasNext = false;
        if (members.size() > offsetLimit.limit()) {
            hasNext = true;
            members.removeLast();
        }

        List<AdminMemberPreviewResponse> memberResponses = AdminMemberPreviewResponse.from(members);

        return PageResponse.<AdminMemberPreviewResponse>builder()
                .contents(memberResponses)
                .hasNext(hasNext)
                .build();
    }

    public AdminMemberPreferenceResponse findMemberPreference(Long memberId) {
        MemberPreference memberPreference = memberPreferenceJpaRepository.findByMemberIdAndStatus(memberId, EntityStatus.ACTIVE)
                .orElseThrow(() -> new HaruHanaException(ErrorType.NOT_FOUND_MEMBER_PREFERENCE));

        return AdminMemberPreferenceResponse.from(memberPreference);
    }

}