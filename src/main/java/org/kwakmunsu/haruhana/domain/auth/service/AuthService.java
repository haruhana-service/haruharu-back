package org.kwakmunsu.haruhana.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.kwakmunsu.haruhana.domain.member.entity.Member;
import org.kwakmunsu.haruhana.domain.member.service.MemberReader;
import org.kwakmunsu.haruhana.global.security.jwt.JwtProvider;
import org.kwakmunsu.haruhana.global.security.jwt.dto.TokenResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final MemberReader memberReader;
    private final JwtProvider jwtProvider;

    @Transactional
    public TokenResponse login(String loginId, String password) {
        Member member = memberReader.findByAccount(loginId, password);

        TokenResponse tokenResponse = jwtProvider.createTokens(member.getId(), member.getRole());
        member.updateRefreshToken(tokenResponse.refreshToken());

        return tokenResponse;
    }

}