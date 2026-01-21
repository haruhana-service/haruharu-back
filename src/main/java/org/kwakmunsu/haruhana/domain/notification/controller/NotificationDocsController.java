package org.kwakmunsu.haruhana.domain.notification.controller;

import static org.kwakmunsu.haruhana.global.support.error.ErrorType.BAD_REQUEST;
import static org.kwakmunsu.haruhana.global.support.error.ErrorType.DEFAULT_ERROR;
import static org.kwakmunsu.haruhana.global.support.error.ErrorType.NOT_FOUND_MEMBER;
import static org.kwakmunsu.haruhana.global.support.error.ErrorType.UNAUTHORIZED_ERROR;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.kwakmunsu.haruhana.domain.notification.controller.dto.FcmTokenRequest;
import org.kwakmunsu.haruhana.global.support.response.ApiResponse;
import org.kwakmunsu.haruhana.global.swagger.ApiExceptions;
import org.springframework.http.ResponseEntity;

@Tag(name = "Notification", description = "알림 관련 API")
public abstract class NotificationDocsController {

    @Operation(
            summary = "FCM 토큰 등록 - JWT [O]",
            description = """
                    ### FCM 토큰을 등록합니다.
                    - PWA에서 발급받은 FCM 토큰을 서버에 등록합니다.
                    - 기존 토큰이 있으면 새 토큰으로 업데이트됩니다.
                    """
    )
    @ApiExceptions(values = {
            BAD_REQUEST,
            UNAUTHORIZED_ERROR,
            NOT_FOUND_MEMBER,
            DEFAULT_ERROR
    })
    public abstract ResponseEntity<ApiResponse<?>> registerFcmToken(FcmTokenRequest request, Long memberId);

    @Operation(
            summary = "FCM 토큰 삭제 - JWT [O]",
            description = """
                    ### FCM 토큰을 삭제합니다.
                    - 로그아웃 시 또는 알림 수신 거부 시 호출합니다.
                    """
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "FCM 토큰 삭제 성공")
    @ApiExceptions(values = {
            UNAUTHORIZED_ERROR,
            NOT_FOUND_MEMBER,
            DEFAULT_ERROR
    })
    public abstract ResponseEntity<Void> deleteFcmToken(Long memberId);

}