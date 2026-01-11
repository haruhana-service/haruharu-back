package org.kwakmunsu.haruhana.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.kwakmunsu.haruhana.domain.member.entity.Member;
import org.kwakmunsu.haruhana.domain.member.enums.Role;
import org.kwakmunsu.haruhana.domain.member.repository.MemberJpaRepository;
import org.kwakmunsu.haruhana.domain.member.service.dto.request.NewProfile;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MemberManager {

    private final MemberJpaRepository memberJpaRepository;

    // TODO: Security 설정 할 떄 암호화 처리
    // NOTE: 온본딩 이후 GUEST -> MEMBER로 전환
    public Member create(NewProfile newProfile) {
        return memberJpaRepository.save(Member.createMember(
                newProfile.loginId(),
                newProfile.password(),
                newProfile.nickname(),
                Role.ROLE_GUEST
        ));
    }

}