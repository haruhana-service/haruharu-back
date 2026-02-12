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
        // NOTE: 현재 회원당 디바이스 토큰은 1명이라 findByMemberId로 처리 가능하지만, 추후 다중 디바이스 지원을 위해 MemberIdAndDeviceToken 기준으로 조회하도록 변경
        memberDeviceJpaRepository.findByMemberId(memberId)
                .ifPresentOrElse(
                        memberDevice -> memberDevice.updateDeviceToken(deviceToken, now),
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
    public void deleteAllByMemberId(Long memberId) {
        // 디바이스 토크은 Hard Delete 처리
        memberDeviceJpaRepository.deleteAllByMemberId(memberId);
    }

}