package org.kwakmunsu.haruhana.domain.member.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.kwakmunsu.haruhana.domain.member.service.dto.request.UpdateProfile;

public record ProfileUpdateRequest(
        @Schema(description = "닉네임", example = "haruhana123")
        @Size(max = 50, message = "닉네임은 최대 50자까지 가능합니다.")
        @NotBlank(message = "닉네임은 비어 있을 수 없습니다.")
        String nickname,

        @Schema(description = "프로필 이미지 S3 객체 키 (Optional)", example = "profiles/haruhana123-profile.png")
        String profileImageKey // presignedUrl 요청 시 받았던 objectKey
) {

    public UpdateProfile toUpdateProfile() {
        return UpdateProfile.builder()
                .nickname(nickname)
                .profileImageKey(profileImageKey)
                .build();
    }

}