package org.kwakmunsu.haruhana.domain.member.repository;

import java.time.LocalDate;
import java.util.List;
import org.kwakmunsu.haruhana.domain.member.entity.MemberPreference;
import org.kwakmunsu.haruhana.global.entity.EntityStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberPreferenceJpaRepository extends JpaRepository<MemberPreference, Long> {

    List<MemberPreference> findAllByEffectiveAtLessThanEqualAndStatus(LocalDate effectiveAt, EntityStatus status);

}