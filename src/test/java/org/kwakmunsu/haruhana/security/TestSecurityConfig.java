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
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/v1/categories").permitAll()
                        .requestMatchers("/v1/auth/login", "/v1/auth/reissue").permitAll()
                        .requestMatchers("/v1/members/sign-up", "/v1/members/nickname", "/v1/members/login-id").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/swagger/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/v1/admin/**").hasRole("ADMIN")
                        .anyRequest().hasRole("MEMBER")
                );

        return http.build();
    }

}