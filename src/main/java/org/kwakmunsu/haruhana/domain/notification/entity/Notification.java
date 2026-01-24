package org.kwakmunsu.haruhana.domain.notification.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.kwakmunsu.haruhana.domain.notification.enums.NotificationType;
import org.kwakmunsu.haruhana.global.entity.BaseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Notification extends BaseEntity {

    @Column(nullable = false)
    private Long memberId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String body;

    @Column(nullable = false)
    private boolean isRead;

    private LocalDateTime sentAt;

    public static Notification create(
            Long memberId,
            NotificationType type,
            String title,
            String body
    ) {
        Notification notification = new Notification();

        notification.memberId = memberId;
        notification.type = type;
        notification.title = title;
        notification.body = body;
        notification.isRead = false;
        notification.sentAt = LocalDateTime.now();

        return notification;
    }

    public void markAsRead() {
        this.isRead = true;
    }

    public boolean isAuthor(Long memberId) {
        return this.memberId.equals(memberId);
    }

}