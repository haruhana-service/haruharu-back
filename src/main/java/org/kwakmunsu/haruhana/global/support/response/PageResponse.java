package org.kwakmunsu.haruhana.global.support.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;

@Schema(description = "페이지 응답 DTO")
@Builder
public record PageResponse<T>(
        @Schema(description = "페이지 내용")
        List<T> contents,

        @Schema(description = "다음 페이지 존재 여부", example = "true")
        boolean hasNext
) {

}