package org.kwakmunsu.haruhana.domain.member.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kwakmunsu.haruhana.domain.member.entity.Member;
import org.kwakmunsu.haruhana.domain.member.entity.MemberPreference;
import org.kwakmunsu.haruhana.domain.member.service.dto.request.NewPreference;
import org.kwakmunsu.haruhana.domain.member.service.dto.request.NewProfile;
import org.kwakmunsu.haruhana.domain.member.service.dto.request.UpdatePreference;
import org.kwakmunsu.haruhana.domain.problem.service.ProblemGenerator;
import org.kwakmunsu.haruhana.domain.streak.service.StreakManager;
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
    private final StreakManager streakManager;
    private final MemberDeviceManager memberDeviceManager;

    @Transactional
    public Long createMember(NewProfile newProfile, NewPreference newPreference) {
        memberValidator.validateNew(newProfile);

        Member member = memberManager.create(newProfile);

        MemberPreference memberPreference = memberManager.registerPreference(member, newPreference);

        streakManager.create(member);

        // 회원가입 시에만 오늘의 문제 문제 직접 생성 - Async 처리
        problemGenerator.generateInitialProblem(member, memberPreference.getCategoryTopic(), memberPreference.getDifficulty());

        log.info("[MemberService] 회원 생성 및 학습 정보 등록 :{}, category: {}, difficulty: {}",
                member.getId(), memberPreference.getCategoryTopic().getName(), newPreference.difficulty());

        return member.getId();
    }


    public MemberProfileResponse getProfile(Long memberId) {
        // memberPreference를 통해 회원 정보와 선호 학습 정보를 함께 조회
        MemberPreference memberPreference = memberReader.getMemberPreference(memberId);

        return MemberProfileResponse.from(memberPreference);
    }

    @Transactional
    public void updatePreference(UpdatePreference updatePreference, Long memberId) {
        MemberPreference memberPreference = memberReader.getMemberPreference(memberId);

        // 기존 선호 학습 정보와 동일한 경우 업데이트하지 않음
        if (memberPreference.isEqualsPreference(updatePreference.categoryTopicId(), updatePreference.difficulty())) {
            return;
        }

        memberManager.updatePreference(memberPreference, updatePreference);
    }

    /**
    * 회원 디바이스 토큰 동기화
    * @param memberId 회원 식별자
    * @param deviceToken 디바이스 토큰
    *
    **/
    public void syncDeviceTokens(Long memberId, String deviceToken) {
        memberDeviceManager.syncDeviceToken(memberId, deviceToken, LocalDateTime.now());
    }

}