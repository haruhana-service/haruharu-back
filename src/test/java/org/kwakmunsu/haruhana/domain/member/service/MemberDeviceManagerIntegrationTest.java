package org.kwakmunsu.haruhana.domain.member.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.kwakmunsu.haruhana.IntegrationTestSupport;
import org.kwakmunsu.haruhana.domain.member.MemberFixture;
import org.kwakmunsu.haruhana.domain.member.entity.MemberDevice;
import org.kwakmunsu.haruhana.domain.member.enums.Role;
import org.kwakmunsu.haruhana.domain.member.repository.MemberDeviceJpaRepository;
import org.kwakmunsu.haruhana.domain.member.repository.MemberJpaRepository;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
class MemberDeviceManagerIntegrationTest extends IntegrationTestSupport {

    static final String DEVICE_TOKEN = "device-token-123";
    static final String NEW_DEVICE_TOKEN = "new-device-token-123";

    final MemberDeviceManager memberDeviceManager;
    final MemberJpaRepository memberJpaRepository;
    final MemberDeviceJpaRepository memberDeviceJpaRepository;

    @Test
    void 새_디바이스_토큰을_저장한다() {
        // given
        LocalDateTime now = LocalDateTime.now();

        var member = MemberFixture.createMemberWithOutId(Role.ROLE_MEMBER);
        memberJpaRepository.save(member);

        // when
        memberDeviceManager.syncDeviceToken(member.getId(), DEVICE_TOKEN, now);

        // then
        var memberDevice = memberDeviceJpaRepository.findByMemberId(member.getId()).orElseThrow();

        assertThat(memberDevice.getMember()).isEqualTo(member);
        assertThat(memberDevice.getDeviceToken()).isEqualTo(DEVICE_TOKEN);
    }

    @Test
    void 이미_등록된_디바이스_토큰이_있다면_토큰과_동기화_시간을_업데이트_한다() {
        // given
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        var member = MemberFixture.createMemberWithOutId(Role.ROLE_MEMBER);
        memberJpaRepository.save(member);

        memberDeviceManager.syncDeviceToken(member.getId(), DEVICE_TOKEN, yesterday);

        // when
        LocalDateTime now = LocalDateTime.now();
        memberDeviceManager.syncDeviceToken(member.getId(), NEW_DEVICE_TOKEN, now);

        // then
        var memberDevice = memberDeviceJpaRepository.findByMemberId(member.getId()).orElseThrow();

        assertThat(memberDevice).extracting(
                MemberDevice::getDeviceToken,
                MemberDevice::getLastSyncedAt
        ).containsExactly(
                NEW_DEVICE_TOKEN,
                now
        );
    }

}