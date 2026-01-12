package org.kwakmunsu.haruhana.domain.member.service;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.kwakmunsu.haruhana.domain.member.entity.Member;
import org.kwakmunsu.haruhana.domain.member.entity.MemberPreference;
import org.kwakmunsu.haruhana.domain.member.repository.MemberJpaRepository;
import org.kwakmunsu.haruhana.domain.member.repository.MemberPreferenceJpaRepository;
import org.kwakmunsu.haruhana.global.entity.EntityStatus;
import org.kwakmunsu.haruhana.global.support.error.ErrorType;
import org.kwakmunsu.haruhana.global.support.error.HaruHanaException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MemberReader {

    private final MemberJpaRepository memberJpaRepository;
    private final MemberPreferenceJpaRepository memberPreferenceJpaRepository;
    private final PasswordEncoder passwordEncoder;

    public Member findByAccount(String loginId, String password) {
        Member member = memberJpaRepository.findByLoginIdAndStatus(loginId, EntityStatus.ACTIVE)
                .orElseThrow(() -> new HaruHanaException(ErrorType.INVALID_ACCOUNT));

        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new HaruHanaException(ErrorType.INVALID_ACCOUNT);
        }

        return member;
    }

    public Member find(Long id) {
        return memberJpaRepository.findByIdAndStatus(id, EntityStatus.ACTIVE)
                .orElseThrow(() -> new HaruHanaException(ErrorType.NOT_FOUND_MEMBER));
    }

    public List<MemberPreference> getMemberPreferences(LocalDate targetDate) {
        return memberPreferenceJpaRepository.findAllByEffectiveAtLessThanEqualAndStatus(targetDate, EntityStatus.ACTIVE);
    }

}