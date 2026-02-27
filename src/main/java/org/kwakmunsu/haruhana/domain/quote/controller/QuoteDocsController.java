package org.kwakmunsu.haruhana.domain.quote.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.kwakmunsu.haruhana.global.support.response.ApiResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "Quote", description = "로그인 화면 문구 관련 API 문서")
public abstract class QuoteDocsController {

    @Operation(
            summary = "챌린지 문구 목록 조회 - JWT [X]",
            description = """
                    ### 로그인 화면에 표시할 문구 목록을 전체 반환합니다.
                    - 인증 없이 누구나 호출할 수 있습니다.
                    - 랜덤 선택은 클라이언트에서 처리합니다.
                    """
    )
    public abstract ResponseEntity<ApiResponse<List<String>>> getQuotes();

}