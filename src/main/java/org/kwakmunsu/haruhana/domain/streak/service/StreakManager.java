package org.kwakmunsu.haruhana.domain.streak.service;

import lombok.RequiredArgsConstructor;
import org.kwakmunsu.haruhana.domain.member.entity.Member;
import org.kwakmunsu.haruhana.domain.streak.entity.Streak;
import org.kwakmunsu.haruhana.domain.streak.repository.StreakJpaRepository;
import org.kwakmunsu.haruhana.global.entity.EntityStatus;
import org.kwakmunsu.haruhana.global.support.error.ErrorType;
import org.kwakmunsu.haruhana.global.support.error.HaruHanaException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class StreakManager {

    private final StreakJpaRepository streakJpaRepository;

    @Transactional
    public void increase(Long memberId) {
        Streak streak = streakJpaRepository.findByMemberIdAndStatus(memberId, EntityStatus.ACTIVE)
                .orElseThrow(() -> new HaruHanaException(ErrorType.NOT_FOUND_STREAK));

        streak.increase();
    }

    @Transactional
    public void initStreakForMember(Member member) {
        Streak streak = streakJpaRepository.findByMemberIdAndStatus(member.getId(), EntityStatus.ACTIVE)
                .orElseThrow(() -> new HaruHanaException(ErrorType.NOT_FOUND_STREAK));

        streak.reset();
    }

}