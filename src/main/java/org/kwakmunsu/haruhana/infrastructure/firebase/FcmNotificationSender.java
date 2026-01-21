package org.kwakmunsu.haruhana.infrastructure.firebase;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kwakmunsu.haruhana.global.support.notification.NotificationSender;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class FcmNotificationSender implements NotificationSender {

    private final FirebaseMessaging firebaseMessaging;

    public void sendNotification(String fcmToken, String title, String body, String type) {
        if (fcmToken == null || fcmToken.isBlank()) {
            log.warn("[FcmService] FCM 토큰이 없어 알림을 발송하지 않습니다.");
            return;
        }

        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        Message message = Message.builder()
                .setToken(fcmToken)
                .setNotification(notification)
                .putData("type", type)
                .build();

        try {
            String response = firebaseMessaging.send(message);
            log.info("[FcmService] 알림 발송 성공. messageId: {}, type: {}", response, type);
        } catch (FirebaseMessagingException e) {
            log.error("[FcmService] 알림 발송 실패. token: {}, type: {}", fcmToken, type, e);
        }
    }

}
