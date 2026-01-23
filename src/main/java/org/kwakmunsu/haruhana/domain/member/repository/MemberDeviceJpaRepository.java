package org.kwakmunsu.haruhana.domain.member.repository;

import java.util.Optional;
import org.kwakmunsu.haruhana.domain.member.entity.MemberDevice;
import org.kwakmunsu.haruhana.global.entity.EntityStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberDeviceJpaRepository extends JpaRepository<MemberDevice, Long> {

    Optional<MemberDevice> findByMemberIdAndDeviceTokenAndStatus(Long memberId, String deviceToken, EntityStatus status);

}