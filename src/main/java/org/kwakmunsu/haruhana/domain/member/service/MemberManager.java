package org.kwakmunsu.haruhana.domain.member.service;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.kwakmunsu.haruhana.domain.category.entity.CategoryTopic;
import org.kwakmunsu.haruhana.domain.category.service.CategoryReader;
import org.kwakmunsu.haruhana.domain.member.entity.Member;
import org.kwakmunsu.haruhana.domain.member.entity.MemberPreference;
import org.kwakmunsu.haruhana.domain.member.enums.Role;
import org.kwakmunsu.haruhana.domain.member.repository.MemberJpaRepository;
import org.kwakmunsu.haruhana.domain.member.repository.MemberPreferenceJpaRepository;
import org.kwakmunsu.haruhana.domain.member.service.dto.request.NewPreference;
import org.kwakmunsu.haruhana.domain.member.service.dto.request.NewProfile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class MemberManager {

    private final MemberReader memberReader;
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
     * GUEST 회원의 학습 정보를 등록합니다.
     * 이 메서드는 GUEST 권한으로 최초 1회만 호출 가능하며, 등록 후 자동으로 MEMBER로 승급됩니다.
     * 따라서 중복 등록은 발생하지 않습니다.
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

    // 해당 메서드는 토큰 탈취 감지 시 초기화 하기위해 사용됨. 트랜잭션 전파 레벨을 아래와 같이 안하면 해당 로직 수행 후 예외 반환을 하는데 그떄 함꼐 롤백되어 초기화가 안됨.
    // 따라서 전파 레벨을 REQUIRES_NEW로 하여금 독립적인 새 트랜잭션을 생성하여 반드시 초기화 되게 함
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void invalidateRefreshToken(Long memberId) {
        Member member = memberReader.find(memberId);
        member.initializeRefreshToken();
    }

}