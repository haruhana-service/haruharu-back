package org.kwakmunsu.haruhana.global.security.jwt.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TokenExpiration {
    // NOTE: 운영 시 2시간으로 변경 예정
    ACCESS_TOKEN  (2000 * 60 * 60 * 1000L),   // 2000시간
    REFRESH_TOKEN (7 * 24 * 60 * 60 * 1000L), // 1주일
  ;

    private final long expirationTime;

}