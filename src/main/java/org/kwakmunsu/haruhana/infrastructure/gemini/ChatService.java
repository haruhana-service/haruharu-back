package org.kwakmunsu.haruhana.infrastructure.gemini;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatClient chatClient;  // Spring AI 에서 제공하는 ChatClient 빈 주입

    // NOTE: 외부 API 호출에 대한 타임아웃 및 재시도 구성이 필요할 수 있음
    public String sendPrompt(String prompt) {
        return chatClient.prompt()
                .user(prompt)
                .options(ChatOptions.builder()
                        .temperature(0.5)
                        .build())
                .call()
                .content();
    }

}