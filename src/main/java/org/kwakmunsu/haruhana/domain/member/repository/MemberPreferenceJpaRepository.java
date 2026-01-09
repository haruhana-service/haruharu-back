package org.kwakmunsu.haruhana.domain.member.repository;

import org.kwakmunsu.haruhana.domain.member.entity.MemberPreference;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberPreferenceJpaRepository extends JpaRepository<MemberPreference, Long> {

}