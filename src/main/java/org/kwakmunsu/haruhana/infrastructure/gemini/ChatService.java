package org.kwakmunsu.haruhana.infrastructure.gemini;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatClient chatClient;  // Spring AI 에서 제공하는 ChatClient 빈 주입

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