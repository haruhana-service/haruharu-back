package org.kwakmunsu.haruhana.domain.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kwakmunsu.haruhana.domain.member.entity.Member;
import org.kwakmunsu.haruhana.domain.member.service.dto.request.NewPreference;
import org.kwakmunsu.haruhana.domain.member.service.dto.request.NewProfile;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberManager memberManager;
    private final MemberReader memberReader;
    private final MemberValidator memberValidator;

    public Long createMember(NewProfile newProfile) {
        memberValidator.validateNew(newProfile);

        Member member = memberManager.create(newProfile);

        log.info("[MemberService] 회원 생성 :{}", member.getId());

        return member.getId();
    }

    public void registerPreference(NewPreference newPreference, Long guestId) {
        Member guest = memberReader.find(guestId);

        memberValidator.validateGuest(guest);

        memberManager.registerPreference(guest, newPreference);

        log.info("[MemberService] 회원 학습 목록 등록 :{}, category : {}, difficulty: {} ",
                guest.getId(), newPreference.categoryTopicId(), newPreference.difficulty());

        // TODO: 회원 학습 목록 첫 등록 후엔 학습 문제 바로 생성 후 할당.
    }

}