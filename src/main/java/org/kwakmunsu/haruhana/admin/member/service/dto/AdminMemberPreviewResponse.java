package org.kwakmunsu.haruhana.admin.member.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import org.kwakmunsu.haruhana.domain.member.entity.Member;
import org.kwakmunsu.haruhana.domain.member.enums.Role;
import org.kwakmunsu.haruhana.global.entity.EntityStatus;

@Schema(description = "관리자 회원 응답 DTO")
public record AdminMemberPreviewResponse(
        Long id,
        String loginId,
        String nickname,
        Role role,
        LocalDateTime lastLoginAt,
        LocalDateTime createdAt,
        EntityStatus status
) {

    public static List<AdminMemberPreviewResponse> from(List<Member> members) {
        return members.stream()
                .map(member -> new AdminMemberPreviewResponse(
                        member.getId(),
                        member.getLoginId(),
                        member.getNickname(),
                        member.getRole(),
                        member.getLastLoginAt(),
                        member.getCreatedAt(),
                        member.getStatus()
                ))
                .toList();

    }

}
