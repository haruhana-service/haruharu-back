package org.kwakmunsu.haruhana.domain.member.service;

import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import org.kwakmunsu.haruhana.global.support.error.ErrorType;
import org.kwakmunsu.haruhana.global.support.error.HaruHanaException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class NicknameFilter {

    private Set<String> badWords;

    @PostConstruct
    void init() throws IOException {
        ClassPathResource resource = new ClassPathResource("bad-words.txt");
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            badWords = reader.lines()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                    .map(line -> line.toLowerCase(Locale.ROOT))
                    .collect(Collectors.toUnmodifiableSet());
        }
    }

    public void validate(String nickname) {
        String normalized = nickname.toLowerCase(Locale.ROOT).replaceAll("\\s+", "");
        for (String badWord : badWords) {
            if (normalized.contains(badWord)) {
                throw new HaruHanaException(ErrorType.INVALID_NICKNAME);
            }
        }
    }

}
