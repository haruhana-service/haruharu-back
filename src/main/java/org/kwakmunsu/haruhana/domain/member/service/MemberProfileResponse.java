package org.kwakmunsu.haruhana.domain.member.service;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import org.kwakmunsu.haruhana.domain.member.entity.MemberPreference;

@Schema(description = "Member 프로필 응답 DTO")
@Builder
public record MemberProfileResponse(
        @Schema(description = "로그인 아이디", example = "haruhana123")
        String loginId,

        @Schema(description = "닉네임", example = "하루하나")
        String nickname,

        @Schema(description = "회원 가입 일시")
        LocalDateTime createdAt,

        @Schema(description = "학습 카테고리 주제 이름", example = "자료구조")
        String categoryTopicName,

        @Schema(description = "학습 문제 난이도", example = "EASY")
        String difficulty,

        @Schema(description = "조회용 presignedUrl", example = "https://example.com/profile-image.jpg")
        String profileImageUrl
) {

    public static MemberProfileResponse from(MemberPreference memberPreference, String profileImageUrl) {
        // NOTE: fetch join 사용으로 N + 1 문제 안터져요!
        return MemberProfileResponse.builder()
                .loginId(memberPreference.getMember().getLoginId())
                .nickname(memberPreference.getMember().getNickname())
                .createdAt(memberPreference.getMember().getCreatedAt())
                .categoryTopicName(memberPreference.getCategoryTopic().getName())
                .difficulty(memberPreference.getDifficulty().name())
                .profileImageUrl(profileImageUrl)
                .build();
    }

}