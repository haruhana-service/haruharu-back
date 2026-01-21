package org.kwakmunsu.haruhana.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.kwakmunsu.haruhana.domain.member.repository.MemberJpaRepository;
import org.kwakmunsu.haruhana.domain.member.service.dto.request.NewProfile;
import org.kwakmunsu.haruhana.global.entity.EntityStatus;
import org.kwakmunsu.haruhana.global.support.error.ErrorType;
import org.kwakmunsu.haruhana.global.support.error.HaruHanaException;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MemberValidator {

    private final MemberJpaRepository memberJpaRepository;

    public void validateNew(NewProfile newProfile) {
        if (memberJpaRepository.existsByLoginIdAndStatus(newProfile.loginId(), EntityStatus.ACTIVE)) {
            throw new HaruHanaException(ErrorType.DUPLICATE_LOGIN_ID);
        }

        if (memberJpaRepository.existsByNicknameAndStatus(newProfile.nickname(), EntityStatus.ACTIVE)) {
            throw new HaruHanaException(ErrorType.DUPLICATE_NICKNAME);
        }
    }

}