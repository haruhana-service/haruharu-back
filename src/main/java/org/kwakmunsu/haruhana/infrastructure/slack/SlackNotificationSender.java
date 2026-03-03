package org.kwakmunsu.haruhana.infrastructure.slack;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.kwakmunsu.haruhana.global.support.notification.ErrorNotificationSender;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class SlackNotificationSender implements ErrorNotificationSender {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final int STACK_TRACE_LINES = 5;

    @Value("${slack.webhook.url:}")
    private String webhookUrl;

    @Async
    public void sendErrorNotification(String message, Throwable throwable) {
        if (!StringUtils.hasText(webhookUrl)) {
            return;
        }

        try {
            String payload = buildPayload(message, throwable);
            sendToSlack(payload);
        } catch (Exception ex) {
            log.warn("Slack 알림 전송 실패: {}", ex.getMessage());
        }
    }

    private String buildPayload(String message, Throwable throwable) {
        String time = LocalDateTime.now().format(FORMATTER);
        String traceId = mdcOrDefault("traceId");
        String httpMethod = mdcOrDefault("httpMethod");
        String requestUri = mdcOrDefault("requestUri");
        String queryString = MDC.get("queryString");
        String clientIp = mdcOrDefault("clientIp");
        String stackTrace = extractStackTrace(throwable);

        String urlLine = queryString != null && !queryString.isBlank()
                ? requestUri + "?" + queryString
                : requestUri;

        String text = String.format(
                "🚨 *[ERROR] 서버 에러 발생*"
                + "\\n• *시간*: %s"
                + "\\n• *traceId*: %s"
                + "\\n• *요청*: `%s %s`"
                + "\\n• *클라이언트 IP*: %s"
                + "\\n• *메세지*: %s"
                + "\\n• *스택 트레이스*:\\n```\\n%s\\n```",
                time, traceId, httpMethod, urlLine, clientIp, message, stackTrace
        );

        return "{\"text\": \"" + text + "\"}";
    }

    private String mdcOrDefault(String key) {
        String value = MDC.get(key);
        return value != null ? value : "N/A";
    }

    private String extractStackTrace(Throwable throwable) {
        if (throwable == null) {
            return "N/A";
        }
        return Arrays.stream(throwable.getStackTrace())
                .limit(STACK_TRACE_LINES)
                .map(StackTraceElement::toString)
                .collect(Collectors.joining("\\n"));
    }

    private void sendToSlack(String payload) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(webhookUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            log.warn("Slack webhook 응답 오류: status={}, body={}", response.statusCode(), response.body());
        }
    }

}
