package org.kwakmunsu.haruhana.admin.member.service;

import lombok.RequiredArgsConstructor;
import org.kwakmunsu.haruhana.admin.member.enums.SortBy;
import org.kwakmunsu.haruhana.admin.member.service.dto.AdminMemberPreviewResponse;
import org.kwakmunsu.haruhana.global.support.OffsetLimit;
import org.kwakmunsu.haruhana.global.support.response.PageResponse;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AdminMemberService {

    private final AdminMemberReader memberReader;

    public  PageResponse<AdminMemberPreviewResponse> findMembers(String search, SortBy sortBy, OffsetLimit offsetLimit) {
       return memberReader.findMembers(search, sortBy, offsetLimit);
    }

}