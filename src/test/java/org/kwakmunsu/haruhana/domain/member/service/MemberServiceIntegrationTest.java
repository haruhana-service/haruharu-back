package org.kwakmunsu.haruhana.domain.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kwakmunsu.haruhana.IntegrationTestSupport;
import org.kwakmunsu.haruhana.domain.category.CategoryFactory;
import org.kwakmunsu.haruhana.domain.category.entity.CategoryTopic;
import org.kwakmunsu.haruhana.domain.category.repository.CategoryTopicJpaRepository;
import org.kwakmunsu.haruhana.domain.member.MemberFixture;
import org.kwakmunsu.haruhana.domain.member.entity.Member;
import org.kwakmunsu.haruhana.domain.member.enums.Role;
import org.kwakmunsu.haruhana.domain.member.repository.MemberJpaRepository;
import org.kwakmunsu.haruhana.domain.member.repository.MemberPreferenceJpaRepository;
import org.kwakmunsu.haruhana.domain.member.service.dto.request.NewPreference;
import org.kwakmunsu.haruhana.domain.member.service.dto.request.UpdateProfile;
import org.kwakmunsu.haruhana.domain.problem.enums.ProblemDifficulty;
import org.kwakmunsu.haruhana.domain.problem.service.ProblemGenerator;
import org.kwakmunsu.haruhana.domain.storage.enums.UploadType;
import org.kwakmunsu.haruhana.domain.storage.repository.StorageJpaRepository;
import org.kwakmunsu.haruhana.domain.storage.service.StorageService;
import org.kwakmunsu.haruhana.domain.streak.repository.StreakJpaRepository;
import org.kwakmunsu.haruhana.domain.streak.service.StreakManager;
import org.kwakmunsu.haruhana.global.entity.EntityStatus;
import org.kwakmunsu.haruhana.global.support.image.StorageProvider;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;

/**
 * MemberService 통합 테스트 - registerPreference 전체 플로우 검증 - 실제 DB 사용 - 이벤트 처리 검증
 */
@RequiredArgsConstructor
@Transactional
class MemberServiceIntegrationTest extends IntegrationTestSupport {

    final CategoryFactory categoryFactory;
    final MemberService memberService;
    final MemberJpaRepository memberJpaRepository;
    final MemberPreferenceJpaRepository memberPreferenceJpaRepository;
    final CategoryTopicJpaRepository categoryTopicJpaRepository;
    final StreakJpaRepository streakJpaRepository;
    final EntityManager entityManager;
    final StorageService storageService;
    final StorageJpaRepository storageJpaRepository;

    @MockitoSpyBean
    StreakManager streakManager;

    @MockitoSpyBean
    ProblemGenerator problemGenerator;

    @MockitoSpyBean
    StorageProvider storageProvider;

    private CategoryTopic categoryTopic;

    @BeforeEach
    void setUp() {
        categoryFactory.deleteAll();
        categoryFactory.saveAll();

        categoryTopic = categoryTopicJpaRepository.findByName("Java")
                .orElseThrow(() -> new RuntimeException("Java 토픽이 존재하지 않습니다"));
    }

    @Test
    void 회원가입_시_회원_학습_설정과_스트릭_생성_문제_생성에_성공한다() {
        // given
        var newProfile = MemberFixture.createNewProfile();
        var newPreference = new NewPreference(categoryTopic.getId(), ProblemDifficulty.EASY);

        // when
        Long memberId = memberService.createMember(newProfile, newPreference);

        // then
        assertThat(memberJpaRepository.findById(memberId).orElseThrow()).isNotNull();
        assertThat(
                memberPreferenceJpaRepository.findByMemberIdAndStatus(memberId, EntityStatus.ACTIVE).orElseThrow()).isNotNull();
        assertThat(streakJpaRepository.findByMemberIdAndStatus(memberId, EntityStatus.ACTIVE).orElseThrow()).isNotNull();
        verify(problemGenerator, times(1)).generateInitialProblem(any(), any(), any());
    }

    @Test
    void 회원_프로필중_닉네임만_변경된다() {
        // given
        var member = MemberFixture.createMemberWithOutId(Role.ROLE_MEMBER);
        memberJpaRepository.save(member);
        assertThat(member).extracting(
                Member::getNickname,
                Member::getProfileImageObjectKey
        ).containsExactly(
                member.getNickname(),
                null
        );

        // when
        var updateProfile = new UpdateProfile("새닉네임", null);
        memberService.updateProfile(updateProfile, member.getId());
        entityManager.flush();

        // then
        var updatedMember = memberJpaRepository.findById(member.getId()).orElseThrow();

        assertThat(updatedMember).extracting(
                Member::getNickname,
                Member::getProfileImageObjectKey
        ).containsExactly(
                updateProfile.nickname(),
                null
        );
    }

    @Test
    void 회원_프로필중_이미지만_변경된다() {
        // given
        var member = MemberFixture.createMemberWithOutId(Role.ROLE_MEMBER);
        memberJpaRepository.save(member);

        // 프로필 이미지 업로드를 위한 presigned url 발급
        var presignedUrlResponse = storageService.createPresignedUrl("filename.png", UploadType.PROFILE_IMAGE, member.getId());
        // 검증
        assertThat(member).extracting(
                Member::getNickname,
                Member::getProfileImageObjectKey
        ).containsExactly(
                member.getNickname(),
                null
        );

        doNothing().when(storageProvider).ensureObjectExists(presignedUrlResponse.objectKey());

        // when
        var updateProfile = new UpdateProfile(member.getNickname(), presignedUrlResponse.objectKey());
        memberService.updateProfile(updateProfile, member.getId());

        // then
        assertThat(member).extracting(
                Member::getNickname,
                Member::getProfileImageObjectKey
        ).containsExactly(
                member.getNickname(),
                presignedUrlResponse.objectKey()
        );
        var storage = storageJpaRepository.findByMemberIdAndObjectKeyAndStatus(
                member.getId(),
                presignedUrlResponse.objectKey(),
                EntityStatus.ACTIVE
        ).orElseThrow();

        assertThat(storage.isComplete()).isTrue();
    }

    @Test
    void 회원_프로필중_닉네임과_이미지가_변경된다() {
        // given
        var member = MemberFixture.createMemberWithOutId(Role.ROLE_MEMBER);
        memberJpaRepository.save(member);
        // 이전 프로필 이미지 업로드 및 완료 처리
        var oldPresignedUrlResponse = storageService.createPresignedUrl("oldFilename.png", UploadType.PROFILE_IMAGE, member.getId());
        doNothing().when(storageProvider).ensureObjectExists(any());
        storageService.completeUpload(oldPresignedUrlResponse.objectKey(), member.getId());

        // 검증
        assertThat(member).extracting(
                Member::getNickname,
                Member::getProfileImageObjectKey
        ).containsExactly(
                member.getNickname(),
                oldPresignedUrlResponse.objectKey()
        );

        // 새로운 프로필 이미지 업로드 준비
        var newPresignedUrlResponse = storageService.createPresignedUrl("newFilename.png", UploadType.PROFILE_IMAGE, member.getId());
        var updateProfile = new UpdateProfile(member.getNickname(), newPresignedUrlResponse.objectKey());

        // when
        memberService.updateProfile(updateProfile, member.getId());

        // then
        assertThat(member).extracting(
                Member::getNickname,
                Member::getProfileImageObjectKey
        ).containsExactly(
                member.getNickname(),
                newPresignedUrlResponse.objectKey()
        );
        var storage = storageJpaRepository.findByMemberIdAndObjectKeyAndStatus(
                member.getId(),
                newPresignedUrlResponse.objectKey(),
                EntityStatus.ACTIVE
        ).orElseThrow();

        assertThat(storage.isComplete()).isTrue();
    }

}