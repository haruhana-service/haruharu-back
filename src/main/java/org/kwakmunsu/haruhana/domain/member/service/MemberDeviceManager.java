package org.kwakmunsu.haruhana.domain.member.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kwakmunsu.haruhana.domain.member.entity.Member;
import org.kwakmunsu.haruhana.domain.member.entity.MemberDevice;
import org.kwakmunsu.haruhana.domain.member.repository.MemberDeviceJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
public class MemberDeviceManager {

    private final MemberReader memberReader;
    private final MemberDeviceJpaRepository memberDeviceJpaRepository;

    @Transactional
    public void syncDeviceToken(Long memberId, String deviceToken, LocalDateTime now) {
        memberDeviceJpaRepository.findByMemberIdAndDeviceToken(memberId, deviceToken)
                .ifPresentOrElse(
                        memberDevice -> memberDevice.updateLastSyncedAt(now),
                        () -> registerNewDeviceToken(memberId, deviceToken, now)
                );
    }

    private void registerNewDeviceToken(Long memberId, String deviceToken, LocalDateTime now) {
        Member member = memberReader.find(memberId);
        memberDeviceJpaRepository.save(MemberDevice.register(
                member,
                deviceToken,
                now
        ));

        log.info("[MemberDeviceManager] new 디바이스 토큰 등록 memberId: {}", memberId);
    }

    @Transactional
    public void deleteDeviceToken(String deviceToken, Long memberId) {
        // 디바이스 토큰은 Hard Delete 처리
        memberDeviceJpaRepository.deleteByMemberIdAndDeviceToken(memberId, deviceToken);
    }

    @Transactional
    public int deleteExpiredTokensBatch(LocalDateTime cutoffDateTime, int batchSize) {
        return memberDeviceJpaRepository.deleteExpiredTokensBatch(cutoffDateTime, batchSize);
    }

}