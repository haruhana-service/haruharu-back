package org.kwakmunsu.haruhana.domain.notification.repository;

import org.kwakmunsu.haruhana.domain.notification.entity.Notification;
import org.kwakmunsu.haruhana.global.entity.EntityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationJpaRepository extends JpaRepository<Notification, Long> {

    @Modifying
    @Query("UPDATE Notification n SET n.status = :status WHERE n.memberId = :memberId")
    void softDeleteByMemberId(@Param("memberId") Long memberId, @Param("status") EntityStatus status);

}