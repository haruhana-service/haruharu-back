package org.kwakmunsu.haruhana.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.kwakmunsu.haruhana.domain.member.entity.Member;
import org.kwakmunsu.haruhana.domain.member.repository.MemberJpaRepository;
import org.kwakmunsu.haruhana.domain.member.service.dto.request.NewProfile;
import org.kwakmunsu.haruhana.domain.member.service.dto.request.UpdateProfile;
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

    public void validateUpdateProfile(UpdateProfile updateProfile, Member member) {
        if (member.hasMatchingNickname(updateProfile.nickname())) {
            return;
        }
        // 내 닉네임 아닐 경우 중복 체크
        if (memberJpaRepository.existsByNicknameAndStatus(updateProfile.nickname(), EntityStatus.ACTIVE)) {
            throw new HaruHanaException(ErrorType.DUPLICATE_NICKNAME);
        }
    }

}