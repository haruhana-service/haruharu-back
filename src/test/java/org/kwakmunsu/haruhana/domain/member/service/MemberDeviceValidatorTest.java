package org.kwakmunsu.haruhana.domain.member.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.kwakmunsu.haruhana.IntegrationTestSupport;
import org.kwakmunsu.haruhana.global.support.error.ErrorType;
import org.kwakmunsu.haruhana.global.support.error.HaruHanaException;

@RequiredArgsConstructor
class MemberDeviceValidatorTest extends IntegrationTestSupport {

    final MemberDeviceValidator memberDeviceValidator;

    private static final String VALID_DEVICE_TOKEN = "cPFKjKe9qbU61b0CwXt0fq:APA91bEk5TKdV8AIaoA1J84YkMgSztA82b12XbAtQwh4HfdoohV6uB_nrFTb6U71td-QHJdCN-BvKs_lkMQMZPoQAuTVofZ6w5mcR8HbK6IAGe0c1iGpJFA";
    private static final String INVALID_DEVICE_TOKEN = "cPFKjKe9qbU61b0CwXt0fq:APA91bEk5TKdV8AIaoA1J84YkMgSztA82b12XbAtQwh4HfdoohV6uB_nrFTb6U71td-QHJdCN-BvKs_lkMQMZPoQAuTVofZ6w5mcR8HbK6IAGe0cinvalid";

    @Test
    void 회원의_디바이스_토큰이_유효한지_검증한다() {
        // when
        memberDeviceValidator.validateDeviceToken(VALID_DEVICE_TOKEN);
    }

    @Test
    void 회원의_디바이스_토큰이_유효하지_않을_경우_예외를_던진다() {
        // when & then
        assertThatThrownBy(() -> memberDeviceValidator.validateDeviceToken(INVALID_DEVICE_TOKEN))
                .isInstanceOf(HaruHanaException.class)
                .hasMessage(ErrorType.INVALID_FCM_TOKEN.getMessage());
    }

}