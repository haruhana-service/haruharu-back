package org.kwakmunsu.haruhana.domain.member.repository;

import java.util.Optional;
import org.kwakmunsu.haruhana.domain.member.entity.MemberDevice;
import org.kwakmunsu.haruhana.global.entity.EntityStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberDeviceJpaRepository extends JpaRepository<MemberDevice, Long> {

    Optional<MemberDevice> findByMemberIdAndDeviceToken(Long memberId, String deviceToken);
    Optional<MemberDevice> findByMemberIdAndStatus(Long memberId, EntityStatus status);
    void deleteByMemberIdAndDeviceToken(Long memberId, String deviceToken);
    boolean existsByMemberIdAndDeviceToken(Long memberId, String deviceToken);

}