package org.kwakmunsu.haruhana.domain.member.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import org.kwakmunsu.haruhana.global.support.error.ErrorType;
import org.kwakmunsu.haruhana.global.support.error.HaruHanaException;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MemberDeviceValidator {

    private final FirebaseMessaging firebaseMessaging;

    public void validateDeviceToken(String deviceToken) {
        try {
            Message message = Message.builder()
                    .setToken(deviceToken)
                    .build();

            // 푸시는 보내지 않고, 토큰 유효성만 검증
            firebaseMessaging.send(message, true /*dry_run*/);
        } catch (FirebaseMessagingException e) {
            throw new HaruHanaException(ErrorType.INVALID_FCM_TOKEN);
        }
    }

}