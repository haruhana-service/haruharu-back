package org.kwakmunsu.haruhana.domain.member.controller;

import static org.kwakmunsu.haruhana.global.support.error.ErrorType.BAD_REQUEST;
import static org.kwakmunsu.haruhana.global.support.error.ErrorType.DEFAULT_ERROR;
import static org.kwakmunsu.haruhana.global.support.error.ErrorType.DUPLICATE_LOGIN_ID;
import static org.kwakmunsu.haruhana.global.support.error.ErrorType.DUPLICATE_NICKNAME;
import static org.kwakmunsu.haruhana.global.support.error.ErrorType.NOT_FOUND_CATEGORY;
import static org.kwakmunsu.haruhana.global.support.error.ErrorType.NOT_FOUND_FCM_TOKEN;
import static org.kwakmunsu.haruhana.global.support.error.ErrorType.NOT_FOUND_MEMBER;
import static org.kwakmunsu.haruhana.global.support.error.ErrorType.UNAUTHORIZED_ERROR;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.kwakmunsu.haruhana.domain.member.controller.dto.DeviceTokenSyncRequest;
import org.kwakmunsu.haruhana.domain.member.controller.dto.MemberCreateRequest;
import org.kwakmunsu.haruhana.domain.member.controller.dto.PreferenceUpdateRequest;
import org.kwakmunsu.haruhana.domain.member.controller.dto.ProfileUpdateRequest;
import org.kwakmunsu.haruhana.domain.member.service.MemberProfileResponse;
import org.kwakmunsu.haruhana.global.support.response.ApiResponse;
import org.kwakmunsu.haruhana.global.swagger.ApiExceptions;
import org.springframework.http.ResponseEntity;

@Tag(name = "Member Docs", description = "Member 관련 API 문서")
public abstract class MemberDocsController {

    @Operation(
            summary = "회원 가입 - JWT [X]",
            description = """
                    ### 새로운 회원을 생성합니다.
                    - 로그인 아이디, 비밀번호, 닉네임을 포함한 요청을 받습니다.
                    - 성공 시 생성된 회원의 ID를 반환합니다.
                    """
    )
    @ApiExceptions(values = {
            BAD_REQUEST,
            DUPLICATE_LOGIN_ID,
            DUPLICATE_NICKNAME,
            DEFAULT_ERROR
    })
    public abstract ResponseEntity<ApiResponse<Long>> create(MemberCreateRequest request);

    @Operation(
            summary = "회원 학습 정보 수정 - JWT [O]",
            description = """
                    ### 회원의 학습 정보를 수정합니다.
                    - 카테고리 주제 ID와 난이도를 포함한 요청을 받습니다.
                    - 성공 시 빈 응답을 반환합니다.
                    - 변경 사항은 다음 날부터 적용됩니다.
                    """
    )
    @ApiExceptions(values = {
            BAD_REQUEST,
            NOT_FOUND_MEMBER,
            NOT_FOUND_CATEGORY,
            DEFAULT_ERROR
    })
    public abstract ResponseEntity<ApiResponse<?>> updatePreference(
            PreferenceUpdateRequest request,
            Long memberId
    );

    @Operation(
            summary = "회원 프로필 조회 - JWT [O]",
            description = """
                    ### 회원의 프로필 정보를 조회합니다.
                    - 회원의 ID를 통해 프로필 정보를 조회합니다.
                    - 성공 시 회원의 프로필 정보를 반환합니다.
                    """
    )
    @ApiExceptions(values = {
            UNAUTHORIZED_ERROR,
            NOT_FOUND_MEMBER,
            DEFAULT_ERROR
    })
    public abstract ResponseEntity<ApiResponse<MemberProfileResponse>> getProfile(Long memberId);

    @Operation(
            summary = "디바이스 토큰 동기화 - JWT [O]",
            description = """
                    ### 디바이스 토큰을 동기화합니다.
                    - 사용자가 사용하는 디바이스의 FCM 토큰을 서버에 동기화합니다.
                    - 로그인 시 또는 디바이스 변경 시 호출됩니다.
                    - 성공 시 빈 응답을 반환합니다.
                    """
    )
    @ApiExceptions(values = {
            BAD_REQUEST,
            UNAUTHORIZED_ERROR,
            NOT_FOUND_MEMBER,
            DEFAULT_ERROR
    })
    public abstract ResponseEntity<ApiResponse<?>> syncDevices(
            DeviceTokenSyncRequest request,
            Long memberId
    );

    @Operation(
            summary = "회원 프로필 수정 - JWT [O]",
            description = """
                    ### 회원의 프로필 정보를 수정합니다.
                    - 닉네임과 프로필 이미지를 포함한 요청을 받습니다.
                    - S3에 프로필 이미지 업로드가 선행되어야 합니다.
                    - s3 업로드가 완료된 프로필 이미지 키를 함께 전달해야 합니다.
                    - 닉네임만 변경 시 프로필 이미지 키는 null로 전달할 수 있습니다.
                    - 성공 시 빈 응답을 반환합니다.
                    """
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "회원 프로필 수정 성공")
    @ApiExceptions(values = {
            BAD_REQUEST,
            DUPLICATE_NICKNAME,
            UNAUTHORIZED_ERROR,
            NOT_FOUND_MEMBER,
            DEFAULT_ERROR
    })
    public abstract ResponseEntity<ApiResponse<?>> updateProfile(
            ProfileUpdateRequest request,
            Long memberId
    );

    @Operation(
            summary = "회원 탈퇴 - JWT [O]",
            description = """
                    ### 회원을 탈퇴 처리합니다.
                    - 회원 및 관련 데이터(스트릭, 제출, 오늘의 문제 등)를 soft delete 처리합니다.
                    - 프로필 이미지가 있는 경우 S3에서도 삭제됩니다.
                    - 성공 시 빈 응답을 반환합니다.
                    """
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "회원 탈퇴 성공")
    @ApiExceptions(values = {
            UNAUTHORIZED_ERROR,
            NOT_FOUND_MEMBER,
            DEFAULT_ERROR
    })
    public abstract ResponseEntity<Void> withdraw(Long memberId);

    @Operation(
            summary = "디바이스 토큰 삭제 - JWT [O]",
            description = """
                    ### 디바이스 토큰을 삭제합니다.
                    - 사용자가 로그아웃하거나 더 이상 푸시 알림을 받지 않으려는 경우 호출됩니다.
                    - 성공 시 빈 응답을 반환합니다.
                    """
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "디바이스 토큰 삭제 성공")
    @ApiExceptions(values = {
            UNAUTHORIZED_ERROR,
            NOT_FOUND_MEMBER,
            NOT_FOUND_FCM_TOKEN,
            DEFAULT_ERROR
    })
    public abstract ResponseEntity<Void> deleteDevices(
            String deviceToken,
            Long memberId
    );

}