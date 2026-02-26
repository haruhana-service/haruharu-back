package org.kwakmunsu.haruhana.admin.member.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Builder;
import org.kwakmunsu.haruhana.domain.member.entity.MemberPreference;

@Schema(description = "관리자 회원 선호도 응답 DTO")
@Builder
public record AdminMemberPreferenceResponse(
        @Schema(description = "선호도 ID", example = "1")
        Long id,

        @Schema(description = "회원 ID", example = "1")
        Long memberId,

        @Schema(description = "카테고리 주제 이름", example = "개발")
        String categoryTopic,

        @Schema(description = "선호도 난이도", example = "상")
        String difficulty,

        @Schema(description = "선호도 효과 발생 날짜", example = "2024-01-01")
        LocalDate effectiveAt
) {

    public static AdminMemberPreferenceResponse from(MemberPreference memberPreference) {
        return AdminMemberPreferenceResponse.builder()
                .id(memberPreference.getId())
                .memberId(memberPreference.getMember().getId())
                .categoryTopic(memberPreference.getCategoryTopic().getName())
                .difficulty(memberPreference.getDifficulty().name())
                .effectiveAt(memberPreference.getEffectiveAt())
                .build();
    }

}