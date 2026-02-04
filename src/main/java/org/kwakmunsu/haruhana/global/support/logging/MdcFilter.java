package org.kwakmunsu.haruhana.global.support.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * MDC(Mapped Diagnostic Context)에 요청 컨텍스트 정보를 설정하는 필터
 * - traceId: 요청 추적을 위한 ID
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MdcFilter extends OncePerRequestFilter {

    private static final String TRACE_ID = "traceId";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            // Request ID와 Trace ID 생성
            String traceId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
            MDC.put(TRACE_ID, traceId);

            // 다음 필터 체인 실행
            filterChain.doFilter(request, response);
        } finally {
            // MDC 클리어 (메모리 누수 방지)
            MDC.clear();
        }
    }
}