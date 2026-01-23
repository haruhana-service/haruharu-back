package org.kwakmunsu.haruhana.domain.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.kwakmunsu.haruhana.global.entity.BaseEntity;

@Table(
        name = "member_devices",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_member_device_token", columnNames = {"member_id", "device_token"})
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class MemberDevice extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Member member;

    @Column(nullable = false, length = 1000)
    private String deviceToken;

    @Column(nullable = false)
    private LocalDateTime lastSyncedAt;

    public static MemberDevice register(Member member, String deviceToken, LocalDateTime now) {
        MemberDevice memberDevice = new MemberDevice();

        memberDevice.member = member;
        memberDevice.deviceToken = deviceToken;
        memberDevice.lastSyncedAt = now;

        return memberDevice;
    }

    public void updateLastSyncedAt(LocalDateTime now) {
        this.lastSyncedAt = now;
    }

}