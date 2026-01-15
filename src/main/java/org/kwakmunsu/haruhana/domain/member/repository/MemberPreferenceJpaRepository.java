package org.kwakmunsu.haruhana.domain.member.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.kwakmunsu.haruhana.domain.member.entity.MemberPreference;
import org.kwakmunsu.haruhana.global.entity.EntityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberPreferenceJpaRepository extends JpaRepository<MemberPreference, Long> {

    List<MemberPreference> findAllByEffectiveAtLessThanEqualAndStatus(LocalDate effectiveAt, EntityStatus status);
    Optional<MemberPreference> findByMemberIdAndStatus(Long memberId, EntityStatus status);

    @Query("SELECT mp FROM MemberPreference mp JOIN FETCH mp.member m WHERE m.id = :memberId AND mp.status = :status")
    Optional<MemberPreference> findByMemberIdWithMember(@Param("memberId") Long memberId, @Param("status") EntityStatus status);
}