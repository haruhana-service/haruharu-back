package org.kwakmunsu.haruhana.domain.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.kwakmunsu.haruhana.domain.member.enums.Role;
import org.kwakmunsu.haruhana.global.entity.BaseEntity;
import org.kwakmunsu.haruhana.global.security.jwt.TokenHasher;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Member extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true, length = 50)
    private String nickname;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    private LocalDateTime lastLoginAt;

    private String refreshToken;

    private String fcmToken;

    public static Member createMember(String loginId, String password, String nickname, Role role) {
        Member member = new Member();

        member.loginId = loginId;
        member.password = password;
        member.nickname = nickname;
        member.role = role;
        member.lastLoginAt = null;
        member.refreshToken = null;

        return member;
    }

    public void updateProfile(String nickname) {
        this.nickname = nickname;
    }

    public void updateLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public void updateRoleToMember() {
        this.role = Role.ROLE_MEMBER;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = TokenHasher.hash(refreshToken);
    }

    public void clearRefreshToken() {
        this.refreshToken = null;
    }

    public boolean isGuest() {
        return role == Role.ROLE_GUEST;
    }

    public void updateFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public void clearFcmToken() {
        this.fcmToken = null;
    }

}