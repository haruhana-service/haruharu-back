package org.kwakmunsu.haruhana.global.support.notification;

public interface NotificationSender {

    void sendNotification(String fcmToken, String title, String body, String type);

}