package org.kwakmunsu.haruhana.domain.member.controller;

import static org.kwakmunsu.haruhana.global.support.error.ErrorType.BAD_REQUEST;
import static org.kwakmunsu.haruhana.global.support.error.ErrorType.DEFAULT_ERROR;
import static org.kwakmunsu.haruhana.global.support.error.ErrorType.DUPLICATE_LOGIN_ID;
import static org.kwakmunsu.haruhana.global.support.error.ErrorType.DUPLICATE_NICKNAME;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.kwakmunsu.haruhana.domain.member.controller.dto.MemberCreateRequest;
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

}