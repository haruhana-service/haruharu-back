package org.kwakmunsu.haruhana.domain.member.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import org.junit.jupiter.api.Test;
import org.kwakmunsu.haruhana.UnitTestSupport;
import org.kwakmunsu.haruhana.global.support.error.ErrorType;
import org.kwakmunsu.haruhana.global.support.error.HaruHanaException;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class MemberDeviceValidatorUnitTest extends UnitTestSupport {

    @Mock
    FirebaseMessaging firebaseMessaging;

    @InjectMocks
    MemberDeviceValidator memberDeviceValidator;

    private static final String VALID_DEVICE_TOKEN = "cPFKjKe9qbU61b0CwXt0fq:APA91bEk5TKdV8AIaoA1J84YkMgSztA82b12XbAtQwh4HfdoohV6uB_nrFTb6U71td-QHJdCN-BvKs_lkMQMZPoQAuTVofZ6w5mcR8HbK6IAGe0c1iGpJFA";
    private static final String INVALID_DEVICE_TOKEN = "cPFKjKe9qbU61b0CwXt0fq:APA91bEk5TKdV8AIaoA1J84YkMgSztA82b12XbAtQwh4HfdoohV6uB_nrFTb6U71td-QHJdCN-BvKs_lkMQMZPoQAuTVofZ6w5mcR8HbK6IAGe0cinvalid";

    @Test
    void 회원의_디바이스_토큰이_유효한지_검증한다() throws FirebaseMessagingException {
        // when
        assertDoesNotThrow(() -> memberDeviceValidator.validateDeviceToken(VALID_DEVICE_TOKEN));

        // then
        verify(firebaseMessaging).send(any(Message.class), any(Boolean.class));
    }

    @Test
    void 회원의_디바이스_토큰이_유효하지_않을_경우_예외를_던진다() throws FirebaseMessagingException {
        // given
        given(firebaseMessaging.send(any(Message.class), any(Boolean.class)))
                .willThrow(FirebaseMessagingException.class);

        // when & then
        assertThatThrownBy(() -> memberDeviceValidator.validateDeviceToken(INVALID_DEVICE_TOKEN))
                .isInstanceOf(HaruHanaException.class)
                .hasMessage(ErrorType.INVALID_FCM_TOKEN.getMessage());
    }

}