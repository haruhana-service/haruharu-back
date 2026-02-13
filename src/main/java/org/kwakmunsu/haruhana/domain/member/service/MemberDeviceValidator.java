package org.kwakmunsu.haruhana.domain.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kwakmunsu.haruhana.domain.member.repository.MemberDeviceJpaRepository;
import org.kwakmunsu.haruhana.global.support.error.ErrorType;
import org.kwakmunsu.haruhana.global.support.error.HaruHanaException;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class MemberDeviceValidator {

    private final MemberDeviceJpaRepository memberDeviceJpaRepository;

    public void validateDeleteDeviceToken(String deviceToken, Long memberId) {
        if (memberDeviceJpaRepository.existsByMemberIdAndDeviceToken(memberId, deviceToken)) {
            return;
        }
        log.warn("[MemberDeviceValidator] 회원의 디바이스 토큰이 존재하지 않아 삭제 불가 - memberId: {}", memberId);
        throw new HaruHanaException(ErrorType.NOT_FOUND_FCM_TOKEN);
    }

}