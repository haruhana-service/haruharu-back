package org.kwakmunsu.haruhana.domain.notification.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.kwakmunsu.haruhana.domain.notification.controller.dto.FcmTokenRequest;
import org.kwakmunsu.haruhana.domain.notification.service.NotificationService;
import org.kwakmunsu.haruhana.global.annotation.LoginMember;
import org.kwakmunsu.haruhana.global.support.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("")
@RestController
public class NotificationController extends NotificationDocsController {

    private final NotificationService notificationService;

    @Override
    @PostMapping("/v1/notifications/fcm-token")
    public ResponseEntity<ApiResponse<?>> registerFcmToken(
            @RequestBody @Valid FcmTokenRequest request,
            @LoginMember Long memberId
    ) {
        notificationService.registerFcmToken(memberId, request.token());

        return ResponseEntity.ok(ApiResponse.success());
    }

    @Override
    @DeleteMapping("/v1/notifications/fcm-token")
    public ResponseEntity<Void> deleteFcmToken(@LoginMember Long memberId) {
        notificationService.deleteFcmToken(memberId);

        return ResponseEntity.noContent().build();
    }

}