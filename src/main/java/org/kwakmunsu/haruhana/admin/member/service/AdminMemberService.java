package org.kwakmunsu.haruhana.admin.member.service;

import lombok.RequiredArgsConstructor;
import org.kwakmunsu.haruhana.admin.member.enums.SortBy;
import org.kwakmunsu.haruhana.admin.member.service.dto.AdminMemberPreferenceResponse;
import org.kwakmunsu.haruhana.admin.member.service.dto.AdminMemberPreviewResponse;
import org.kwakmunsu.haruhana.domain.member.enums.Role;
import org.kwakmunsu.haruhana.global.support.OffsetLimit;
import org.kwakmunsu.haruhana.global.support.response.PageResponse;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AdminMemberService {

    private final AdminMemberReader memberReader;
    private final AdminMemberManager memberManager;

    public PageResponse<AdminMemberPreviewResponse> findMembers(String search, SortBy sortBy, OffsetLimit offsetLimit) {
        return memberReader.findMembers(search, sortBy, offsetLimit);
    }

    public AdminMemberPreferenceResponse findMemberPreference(Long memberId) {
        return memberReader.findMemberPreference(memberId);
    }

    public void updateRole(Long memberId, Role role) {
        memberManager.updateMemberRole(memberId, role);
    }

    public void deleteMember(Long memberId) {
        memberManager.deleteMember(memberId);
    }

}