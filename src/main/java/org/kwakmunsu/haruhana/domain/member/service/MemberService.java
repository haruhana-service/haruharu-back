package org.kwakmunsu.haruhana.domain.member.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kwakmunsu.haruhana.domain.member.entity.Member;
import org.kwakmunsu.haruhana.domain.member.entity.MemberPreference;
import org.kwakmunsu.haruhana.domain.member.service.dto.request.NewPreference;
import org.kwakmunsu.haruhana.domain.member.service.dto.request.NewProfile;
import org.kwakmunsu.haruhana.domain.member.service.dto.request.UpdatePreference;
import org.kwakmunsu.haruhana.domain.member.service.dto.request.UpdateProfile;
import org.kwakmunsu.haruhana.domain.problem.service.ProblemGenerator;
import org.kwakmunsu.haruhana.domain.storage.service.StorageManager;
import org.kwakmunsu.haruhana.domain.streak.service.StreakManager;
import org.kwakmunsu.haruhana.global.support.image.StorageProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberService {

    // 의존성이 너무 많이 엮여있는데 리팩토링이 필요해 보임
    private final MemberManager memberManager;
    private final MemberReader memberReader;
    private final MemberValidator memberValidator;
    private final ProblemGenerator problemGenerator;
    private final StreakManager streakManager;
    private final MemberDeviceManager memberDeviceManager;
    private final MemberDeviceValidator memberDeviceValidator;
    private final StorageManager storageManager;
    private final StorageProvider storageProvider;

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

        String profileImageObjectKey = memberPreference.getMember().getProfileImageObjectKey();
        String presignedReadUrl = profileImageObjectKey != null
                ? storageProvider.generatePresignedReadUrl(profileImageObjectKey)
                : null;

        return MemberProfileResponse.from(memberPreference, presignedReadUrl);
    }

    /**
     * 회원 프로필 업데이트 <br>
     * 회원 프로필 이미지가 변경된 경우 StorageManager를 통해 업로드 완료 처리 및 이전 이미지 S3 삭제 처리
     * @param updateProfile 회원 프로필 업데이트 정보
     * @param memberId 회원 식별자
     *
    * */
    @Transactional
    public void updateProfile(UpdateProfile updateProfile, Long memberId) {
        Member member = memberReader.find(memberId);
        memberValidator.validateUpdateProfile(updateProfile, member);

        storageManager.completeUpload(updateProfile.profileImageKey(), member);

        member.updateProfile(updateProfile.nickname());
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
        memberDeviceValidator.validateDeviceToken(deviceToken);

        memberDeviceManager.syncDeviceToken(memberId, deviceToken, LocalDateTime.now());
    }

    /**
     * 회원 디바이스 토큰 삭제
     * @param memberId 회원 식별자
     * - hard delete 처리
     **/
    public void deleteDeviceTokens(Long memberId) {
        memberDeviceManager.deleteAllByMemberId(memberId);
    }

}