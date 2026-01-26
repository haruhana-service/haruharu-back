package org.kwakmunsu.haruhana.domain.member.service.dto.request;

import lombok.Builder;

@Builder
public record UpdateProfile(
        String nickname,
        String profileImageKey
) {

}