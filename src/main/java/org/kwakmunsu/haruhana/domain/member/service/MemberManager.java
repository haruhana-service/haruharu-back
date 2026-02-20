package org.kwakmunsu.haruhana.domain.member.service;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kwakmunsu.haruhana.domain.category.entity.CategoryTopic;
import org.kwakmunsu.haruhana.domain.category.service.CategoryReader;
import org.kwakmunsu.haruhana.domain.member.entity.Member;
import org.kwakmunsu.haruhana.domain.member.entity.MemberPreference;
import org.kwakmunsu.haruhana.domain.member.enums.Role;
import org.kwakmunsu.haruhana.domain.member.repository.MemberJpaRepository;
import org.kwakmunsu.haruhana.domain.member.repository.MemberPreferenceJpaRepository;
import org.kwakmunsu.haruhana.domain.member.service.dto.request.NewPreference;
import org.kwakmunsu.haruhana.domain.member.service.dto.request.NewProfile;
import org.kwakmunsu.haruhana.domain.member.service.dto.request.UpdatePreference;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
public class MemberManager {

    private final MemberJpaRepository memberJpaRepository;
    private final MemberPreferenceJpaRepository memberPreferenceJpaRepository;
    private final CategoryReader categoryReader;
    private final PasswordEncoder passwordEncoder;

    public Member create(NewProfile newProfile) {
        return memberJpaRepository.save(Member.createMember(
                newProfile.loginId(),
                passwordEncoder.encode(newProfile.password()),
                newProfile.nickname(),
                Role.ROLE_MEMBER
        ));
    }

    public MemberPreference registerPreference(Member member, NewPreference newPreference) {
        CategoryTopic categoryTopic = categoryReader.findCategoryTopic(newPreference.categoryTopicId());

        return memberPreferenceJpaRepository.save(MemberPreference.create(
                member,
                categoryTopic,
                newPreference.difficulty(),
                LocalDate.now() // 첫 등록은 등록 날짜로 고정
        ));
    }

    @Transactional
    public void updatePreference(MemberPreference memberPreference, UpdatePreference updatePreference) {
        Member member = memberPreference.getMember();
        CategoryTopic categoryTopic = categoryReader.findCategoryTopic(updatePreference.categoryTopicId());

        // 같은 날짜에 회원 설정 변경 시 기존 설정을 업데이트하는 방식으로 처리
        if (memberPreference.isEffectiveToday()) {
            memberPreference.updatePreference(categoryTopic, updatePreference.difficulty());
        } else {
            // 다음 날부터 적용되는 설정 변경 시 기존 설정을 삭제하고 새로운 설정을 생성하는 방식으로 처리
            preferenceUpdateForTomorrow(memberPreference, member, categoryTopic, updatePreference);
        }

        log.info("[MemberManager] 회원 학습 목록 수정 - 회원 id: {}, category: {}, difficulty: {} ",
                member.getId(), updatePreference.categoryTopicId(), updatePreference.difficulty());
    }

    public void updateRefreshToken(Member member, String refreshToken) {
        member.updateRefreshToken(refreshToken);
    }

    public void clearMember(Member member) {
        member.clearRefreshToken();
    }

    private void preferenceUpdateForTomorrow(
            MemberPreference memberPreference,
            Member member,
            CategoryTopic categoryTopic,
            UpdatePreference updatePreference
    ) {
        memberPreference.delete();
        memberPreferenceJpaRepository.flush(); // soft delete 즉시 반영

        memberPreferenceJpaRepository.save(MemberPreference.create(
                member,
                categoryTopic,
                updatePreference.difficulty(),
                LocalDate.now().plusDays(1)
        ));
    }

}