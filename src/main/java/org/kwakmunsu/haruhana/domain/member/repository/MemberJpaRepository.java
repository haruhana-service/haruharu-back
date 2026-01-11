package org.kwakmunsu.haruhana.domain.member.repository;

import java.util.Optional;
import org.kwakmunsu.haruhana.domain.member.entity.Member;
import org.kwakmunsu.haruhana.global.entity.EntityStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberJpaRepository extends JpaRepository<Member, Long> {

    boolean existsByLoginIdAndStatus(String loginId, EntityStatus status);
    boolean existsByNicknameAndStatus(String nickname, EntityStatus status);
    Optional<Member> findByLoginIdAndStatus(String loginId, EntityStatus status);
    Optional<Member> findByIdAndStatus(Long id, EntityStatus entityStatus);

}