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
    private final NicknameFilter nicknameFilter;

    public void validateNew(NewProfile newProfile) {
        if (memberJpaRepository.existsByLoginIdAndStatus(newProfile.loginId(), EntityStatus.ACTIVE)) {
            throw new HaruHanaException(ErrorType.DUPLICATE_LOGIN_ID);
        }
        validateNicknameAvailable(newProfile.nickname());
    }

    public void validateUpdateProfile(UpdateProfile updateProfile, Member member) {
        nicknameFilter.validate(updateProfile.nickname());
        if (member.hasMatchingNickname(updateProfile.nickname())) {
            return;
        }
        if (memberJpaRepository.existsByNicknameAndStatus(updateProfile.nickname(), EntityStatus.ACTIVE)) {
            throw new HaruHanaException(ErrorType.DUPLICATE_NICKNAME);
        }
    }

    public void validateNicknameAvailable(String nickname) {
        nicknameFilter.validate(nickname);
        if (memberJpaRepository.existsByNicknameAndStatus(nickname, EntityStatus.ACTIVE)) {
            throw new HaruHanaException(ErrorType.DUPLICATE_NICKNAME);
        }
    }

}