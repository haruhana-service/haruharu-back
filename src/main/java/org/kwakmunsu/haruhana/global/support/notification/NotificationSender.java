package org.kwakmunsu.haruhana.global.support.notification;

import org.kwakmunsu.haruhana.domain.notification.enums.NotificationType;

public interface NotificationSender {

    void sendNotification(String fcmToken, String title, String body, NotificationType type);

}