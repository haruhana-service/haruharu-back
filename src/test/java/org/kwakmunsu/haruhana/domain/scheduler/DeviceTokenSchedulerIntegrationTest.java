package org.kwakmunsu.haruhana.domain.scheduler;

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
class DeviceTokenSchedulerIntegrationTest extends IntegrationTestSupport {

    final MemberJpaRepository memberJpaRepository;
    final MemberDeviceJpaRepository memberDeviceJpaRepository;
    final DeviceTokenScheduler deviceTokenScheduler;

    @Test
    void 일정기간_동안_사용되지_않은_DeviceToken을_삭제한다() {
        // given
        var member = MemberFixture.createMemberWithOutId(Role.ROLE_MEMBER);
        memberJpaRepository.save(member);
        memberDeviceJpaRepository.save(MemberDevice.register(
                member,
                "expired-device-token",
                LocalDateTime.now().minusDays(30))
        );

        // when
        deviceTokenScheduler.clearExpiredDeviceToken();

        // then
        assertThat(memberDeviceJpaRepository.findByMemberIdAndDeviceToken(member.getId(), "active-device-token"))
                .isEmpty();
    }

    @Test
    void 만료되지_않은_DeviceToken을_삭제되지_않는다() {
        // given
        var member = MemberFixture.createMemberWithOutId(Role.ROLE_MEMBER);
        memberJpaRepository.save(member);
        memberDeviceJpaRepository.save(MemberDevice.register(
                member,
                "active-device-token",
                LocalDateTime.now().minusDays(10))
        );

        // when
        deviceTokenScheduler.clearExpiredDeviceToken();

        // then
        assertThat(memberDeviceJpaRepository.findByMemberIdAndDeviceToken(member.getId(), "active-device-token"))
                .isPresent();
    }

}