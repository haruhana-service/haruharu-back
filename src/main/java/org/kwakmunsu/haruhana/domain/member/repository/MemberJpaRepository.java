package org.kwakmunsu.haruhana.domain.member.repository;

import org.kwakmunsu.haruhana.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberJpaRepository extends JpaRepository<Member, Long> {

}