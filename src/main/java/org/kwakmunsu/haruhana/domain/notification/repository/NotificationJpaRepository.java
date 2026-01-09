package org.kwakmunsu.haruhana.domain.notification.repository;

import org.kwakmunsu.haruhana.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationJpaRepository extends JpaRepository<Notification, Long> {

}