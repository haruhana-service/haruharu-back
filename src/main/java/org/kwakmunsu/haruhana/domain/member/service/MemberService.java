package org.kwakmunsu.haruhana.domain.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kwakmunsu.haruhana.domain.member.entity.Member;
import org.kwakmunsu.haruhana.domain.member.entity.MemberPreference;
import org.kwakmunsu.haruhana.domain.member.service.dto.request.NewPreference;
import org.kwakmunsu.haruhana.domain.member.service.dto.request.NewProfile;
import org.kwakmunsu.haruhana.domain.problem.service.ProblemGenerator;
import org.kwakmunsu.haruhana.domain.streak.event.StreakCreateEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberManager memberManager;
    private final MemberReader memberReader;
    private final MemberValidator memberValidator;
    private final ProblemGenerator problemGenerator;
    private final ApplicationEventPublisher eventPublisher;

    public Long createMember(NewProfile newProfile) {
        memberValidator.validateNew(newProfile);

        Member member = memberManager.create(newProfile);

        log.info("[MemberService] 회원 생성 :{}", member.getId());

        return member.getId();
    }

    @Transactional
    public void registerPreference(NewPreference newPreference, Long guestId) {
        Member guest = memberReader.find(guestId);
        memberValidator.validateGuest(guest);
        MemberPreference memberPreference = memberManager.registerPreference(guest, newPreference);

        log.info("[MemberService] 회원 학습 목록 등록. 회원 id: {}, category: {}, difficulty: {} ",
                guest.getId(), newPreference.categoryTopicId(), newPreference.difficulty());

        // NOTE: 오늘의 문제 생성 - 회원가입 후 학습 정보 등록 시에만 첫 문제 직접 생성
        problemGenerator.generateInitialProblem(guest, memberPreference.getCategoryTopic(), memberPreference.getDifficulty());

        // NOTE: streak 생성 이벤트 발행 - Async 처리
        eventPublisher.publishEvent(new StreakCreateEvent(guest));
    }

}