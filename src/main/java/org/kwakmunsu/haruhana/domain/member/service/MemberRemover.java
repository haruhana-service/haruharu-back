package org.kwakmunsu.haruhana.domain.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kwakmunsu.haruhana.domain.dailyproblem.repository.DailyProblemJpaRepository;
import org.kwakmunsu.haruhana.domain.member.entity.Member;
import org.kwakmunsu.haruhana.domain.member.repository.MemberDeviceJpaRepository;
import org.kwakmunsu.haruhana.domain.member.repository.MemberPreferenceJpaRepository;
import org.kwakmunsu.haruhana.domain.notification.repository.NotificationJpaRepository;
import org.kwakmunsu.haruhana.domain.storage.repository.StorageJpaRepository;
import org.kwakmunsu.haruhana.domain.streak.repository.StreakJpaRepository;
import org.kwakmunsu.haruhana.domain.submission.repository.SubmissionJpaRepository;
import org.kwakmunsu.haruhana.global.entity.EntityStatus;
import org.kwakmunsu.haruhana.global.support.image.StorageProvider;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
public class MemberRemover {

    private final MemberReader memberReader;
    private final StreakJpaRepository streakJpaRepository;
    private final MemberDeviceJpaRepository memberDeviceJpaRepository;
    private final SubmissionJpaRepository submissionJpaRepository;
    private final DailyProblemJpaRepository dailyProblemJpaRepository;
    private final MemberPreferenceJpaRepository memberPreferenceJpaRepository;
    private final StorageJpaRepository storageJpaRepository;
    private final NotificationJpaRepository notificationJpaRepository;
    private final StorageProvider storageProvider;

    @Transactional
    public void remove(Long memberId) {
        Member member = memberReader.find(memberId);

        streakJpaRepository.softDeleteByMemberId(memberId, EntityStatus.DELETED);
        memberDeviceJpaRepository.deleteAllByMemberId(memberId);
        submissionJpaRepository.softDeleteByMemberId(memberId, EntityStatus.DELETED);
        dailyProblemJpaRepository.softDeleteByMemberId(memberId, EntityStatus.DELETED);
        memberPreferenceJpaRepository.softDeleteByMemberId(memberId, EntityStatus.DELETED);
        storageJpaRepository.softDeleteByMemberId(memberId, EntityStatus.DELETED);
        notificationJpaRepository.softDeleteByMemberId(memberId, EntityStatus.DELETED);

        if (member.getProfileImageObjectKey() != null) {
            storageProvider.deleteObjectAsync(member.getProfileImageObjectKey());
        }

        member.clearRefreshToken();
        member.delete();
    }

}
