package org.kwakmunsu.haruhana.security;

import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.kwakmunsu.haruhana.security.annotation.TestGuest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

@Slf4j
public class TestGuestSecurityContextFactory implements WithSecurityContextFactory<TestGuest> {

    @Override
    public SecurityContext createSecurityContext(TestGuest annotation) {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        GrantedAuthority authority = new SimpleGrantedAuthority(annotation.role());
        String guestId = String.valueOf(annotation.id());

        log.info("Guest ID: {}, Role: {}", guestId, authority);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                guestId,
                null,
                Collections.singletonList(authority)
        );
        securityContext.setAuthentication(authentication);

        return securityContext;
    }

}