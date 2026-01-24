package org.kwakmunsu.haruhana.domain.notification.controller;

import lombok.RequiredArgsConstructor;
import org.kwakmunsu.haruhana.domain.notification.service.NotificationService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("")
@RestController
public class NotificationController extends NotificationDocsController {

    private final NotificationService notificationService;

}