package org.kwakmunsu.haruhana.domain.member.service.dto.request;

import lombok.Builder;

@Builder
public record NewProfile(
        String loginId,
        String password,
        String nickname
) {

}