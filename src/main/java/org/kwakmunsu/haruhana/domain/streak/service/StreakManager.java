package org.kwakmunsu.haruhana.domain.streak.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kwakmunsu.haruhana.domain.member.entity.Member;
import org.kwakmunsu.haruhana.domain.streak.entity.Streak;
import org.kwakmunsu.haruhana.domain.streak.repository.StreakJpaRepository;
import org.kwakmunsu.haruhana.global.entity.EntityStatus;
import org.kwakmunsu.haruhana.global.support.error.ErrorType;
import org.kwakmunsu.haruhana.global.support.error.HaruHanaException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
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

    // 첫 회원 가입 시 사용되는 메서드.
    // 스트릭 생성 혹시 생성되었다면 아무런 일도 하지 않음
    public void create(Member guest) {
        if (streakJpaRepository.existsByMemberIdAndStatus(guest.getId(), EntityStatus.ACTIVE)) {
            log.info("[StreakManager] 이미 스트릭이 존재하여 생성하지 않음 - memberId: {}", guest.getId());
            return;
        }
        streakJpaRepository.save(Streak.create(guest));
    }

}