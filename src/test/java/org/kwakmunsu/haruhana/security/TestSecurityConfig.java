package org.kwakmunsu.haruhana.security;

import lombok.RequiredArgsConstructor;
import org.kwakmunsu.haruhana.domain.member.enums.Role;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor
@EnableWebSecurity
@TestConfiguration
public class TestSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/v1/auth/**",
                                "/v1/members/sign-up",
                                "/health",
                                "/test/**",
                                "/"
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST, "/v1/members/preferences").hasAuthority(Role.ROLE_GUEST.name())
                        .anyRequest().authenticated()
                );

        return http.build();
    }

}