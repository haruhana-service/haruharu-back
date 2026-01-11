package org.kwakmunsu.haruhana.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.kwakmunsu.haruhana.domain.member.entity.Member;
import org.kwakmunsu.haruhana.domain.member.repository.MemberJpaRepository;
import org.kwakmunsu.haruhana.global.entity.EntityStatus;
import org.kwakmunsu.haruhana.global.support.error.ErrorType;
import org.kwakmunsu.haruhana.global.support.error.HaruHanaException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MemberReader {

    private final MemberJpaRepository memberJpaRepository;
    private final PasswordEncoder passwordEncoder;

    public Member findByAccount(String loginId, String password) {
        Member member = memberJpaRepository.findByLoginIdAndStatus(loginId, EntityStatus.ACTIVE)
                .orElseThrow(() -> new HaruHanaException(ErrorType.INVALID_ACCOUNT));

        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new HaruHanaException(ErrorType.INVALID_ACCOUNT);
        }

        return member;
    }

}