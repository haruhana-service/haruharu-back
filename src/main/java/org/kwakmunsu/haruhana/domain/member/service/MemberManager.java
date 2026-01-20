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

    // NOTE: 온본딩 이후 GUEST -> MEMBER로 전환
    public Member create(NewProfile newProfile) {
        return memberJpaRepository.save(Member.createMember(
                newProfile.loginId(),
                passwordEncoder.encode(newProfile.password()),
                newProfile.nickname(),
                Role.ROLE_GUEST
        ));
    }

    /**
     * GUEST 회원의 학습 정보를 등록합니다. 이 메서드는 GUEST 권한으로 최초 1회만 호출 가능하며, 등록 후 자동으로 MEMBER로 승급됩니다. 따라서 중복 등록은 발생하지 않습니다.
     */
    public MemberPreference registerPreference(Member guest, NewPreference newPreference) {
        CategoryTopic categoryTopic = categoryReader.findCategoryTopic(newPreference.categoryTopicId());

        MemberPreference memberPreference = memberPreferenceJpaRepository.save(MemberPreference.create(
                guest,
                categoryTopic,
                newPreference.difficulty(),
                LocalDate.now() // 첫 등록은 등록 날짜로 고정
        ));

        // 학습 정보 등록 후 GUEST -> MEMBER로 변경
        guest.updateRoleToMember();

        return memberPreference;
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