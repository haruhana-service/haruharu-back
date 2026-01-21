package org.kwakmunsu.haruhana.domain.notification.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kwakmunsu.haruhana.domain.member.entity.Member;
import org.kwakmunsu.haruhana.domain.member.service.MemberReader;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationManager {

    private final MemberReader memberReader;

    @Transactional
    public void registerFcmToken(Long memberId, String fcmToken) {
        Member member = memberReader.find(memberId);
        member.updateFcmToken(fcmToken);

        log.info("[NotificationService] FCM 토큰 등록 완료. memberId: {}", memberId);
    }

    @Transactional
    public void deleteFcmToken(Long memberId) {
        Member member = memberReader.find(memberId);
        member.clearFcmToken();

        log.info("[NotificationService] FCM 토큰 삭제 완료. memberId: {}", memberId);
    }

}