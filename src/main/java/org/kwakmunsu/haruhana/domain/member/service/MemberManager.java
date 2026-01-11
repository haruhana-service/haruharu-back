package org.kwakmunsu.haruhana.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.kwakmunsu.haruhana.domain.member.entity.Member;
import org.kwakmunsu.haruhana.domain.member.enums.Role;
import org.kwakmunsu.haruhana.domain.member.repository.MemberJpaRepository;
import org.kwakmunsu.haruhana.domain.member.service.dto.request.NewProfile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MemberManager {

    private final MemberJpaRepository memberJpaRepository;
    private final PasswordEncoder passwordEncoder;

    // NOTE: 온본딩 이후 GUEST -> MEMBER로 전환
    public Member create(NewProfile newProfile) {
        return memberJpaRepository.save(Member.createMember(
                newProfile.loginId(),
                passwordEncoder.encode(newProfile.password()),
                newProfile.nickname(),
                Role.ROLE_GUEST
        ));
    }

}