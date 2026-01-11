package org.kwakmunsu.haruhana.domain.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kwakmunsu.haruhana.domain.member.entity.Member;
import org.kwakmunsu.haruhana.domain.member.service.dto.request.NewProfile;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberManager memberManager;
    private final MemberValidator memberValidator;

    public Long createMember(NewProfile newProfile) {
        memberValidator.validateNew(newProfile);

        Member member = memberManager.create(newProfile);

        log.info("[MemberService] 회원 생성 :{}", member.getId());

        return member.getId();
    }

}