package org.kwakmunsu.haruhana.admin.member.service;

import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Comment;
import org.kwakmunsu.haruhana.domain.member.entity.Member;
import org.kwakmunsu.haruhana.domain.member.enums.Role;
import org.kwakmunsu.haruhana.domain.member.repository.MemberJpaRepository;
import org.kwakmunsu.haruhana.global.entity.EntityStatus;
import org.kwakmunsu.haruhana.global.support.error.ErrorType;
import org.kwakmunsu.haruhana.global.support.error.HaruHanaException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class AdminMemberManager {

    private final MemberJpaRepository memberJpaRepository;

    @Transactional
    public void updateMemberRole(Long memberId, Role role) {
        Member member = memberJpaRepository.findByIdAndStatus(memberId, EntityStatus.ACTIVE)
                .orElseThrow(() -> new HaruHanaException(ErrorType.NOT_FOUND_MEMBER));

        member.updateRole(role);
    }

}