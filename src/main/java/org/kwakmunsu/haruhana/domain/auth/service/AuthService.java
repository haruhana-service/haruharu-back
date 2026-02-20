package org.kwakmunsu.haruhana.domain.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kwakmunsu.haruhana.domain.member.entity.Member;
import org.kwakmunsu.haruhana.domain.member.service.MemberManager;
import org.kwakmunsu.haruhana.domain.member.service.MemberReader;
import org.kwakmunsu.haruhana.global.security.jwt.JwtProvider;
import org.kwakmunsu.haruhana.global.security.jwt.dto.TokenResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final MemberManager memberManager;
    private final MemberReader memberReader;
    private final JwtProvider jwtProvider;

    @Transactional
    public TokenResponse login(String loginId, String password) {
        Member member = memberReader.findByAccount(loginId, password);

        TokenResponse tokenResponse = jwtProvider.createTokens(member.getId(), member.getRole());
        memberManager.updateRefreshToken(member, tokenResponse.refreshToken());

        log.info("[AuthService] 로그인 성공. memberId: {}", member.getId());

        return tokenResponse;
    }

    @Transactional
    public TokenResponse reissue(String refreshToken) {
        Member member = memberReader.findByRefreshToken(refreshToken);

        TokenResponse tokenResponse = jwtProvider.createTokens(member.getId(), member.getRole());
        memberManager.updateRefreshToken(member, tokenResponse.refreshToken());

        log.info("[AuthService] 토큰 재발급 성공. memberId: {}", member.getId());

        return tokenResponse;
    }

    // NOTE: 추후 Access Token 블랙리스트 처리 고려
    @Transactional
    public void logout(Long memberId) {
        Member member = memberReader.find(memberId);
        memberManager.clearMember(member);

        log.info("[AuthService] 로그아웃 성공. memberId: {}", member.getId());
    }

}