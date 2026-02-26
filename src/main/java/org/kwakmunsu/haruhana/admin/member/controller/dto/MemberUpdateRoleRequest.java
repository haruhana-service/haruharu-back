package org.kwakmunsu.haruhana.admin.member.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import org.kwakmunsu.haruhana.domain.member.enums.Role;

@Schema(description = "회원 권한 업데이트 요청 DTO")
public record MemberUpdateRoleRequest(
        @Schema(description = "업데이트할 권한", example = "ROLE_ADMIN")
        @NotNull(message = "권한은 필수입니다.")
        Role role
) {

}