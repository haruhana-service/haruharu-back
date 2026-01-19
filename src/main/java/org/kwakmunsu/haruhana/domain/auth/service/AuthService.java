package org.kwakmunsu.haruhana.domain.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kwakmunsu.haruhana.domain.member.entity.Member;
import org.kwakmunsu.haruhana.domain.member.service.MemberManager;
import org.kwakmunsu.haruhana.domain.member.service.MemberReader;
import org.kwakmunsu.haruhana.global.security.jwt.JwtProvider;
import org.kwakmunsu.haruhana.global.security.jwt.dto.TokenResponse;
import org.kwakmunsu.haruhana.global.support.error.ErrorType;
import org.kwakmunsu.haruhana.global.support.error.HaruHanaException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final MemberReader memberReader;
    private final MemberManager memberManager;
    private final JwtProvider jwtProvider;

    @Transactional
    public TokenResponse login(String loginId, String password) {
        Member member = memberReader.findByAccount(loginId, password);

        TokenResponse tokenResponse = jwtProvider.createTokens(member.getId(), member.getRole());
        member.updateRefreshToken(tokenResponse.refreshToken());

        log.info("[AuthService] 로그인 성공. memberId: {}", member.getId());

        return tokenResponse;
    }

    @Transactional
    public TokenResponse reissue(String refreshToken, Long memberId) {
        Member member = memberReader.find(memberId);

        // 탈취 감지로 refreshToken 비활성화 후 예외 반환
        if (!member.isEqualsRefreshToken(refreshToken)) {
            memberManager.invalidateRefreshToken(memberId);
            throw new HaruHanaException(ErrorType.TOKEN_THEFT_DETECTED);
        }

        TokenResponse tokenResponse = jwtProvider.createTokens(member.getId(), member.getRole());
        member.updateRefreshToken(refreshToken);

        log.info("[AuthService] 토큰 재발급 성공. memberId: {}", member.getId());

        return tokenResponse;
    }

}