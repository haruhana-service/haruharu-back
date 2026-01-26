package org.kwakmunsu.haruhana.domain.storage.enums;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileContentType {

    // 이미지
    JPEG ("jpeg", "image/jpeg", FileCategory.IMAGE),
    JPG  ("jpg", "image/jpeg", FileCategory.IMAGE),
    PNG  ("png", "image/png", FileCategory.IMAGE),
    GIF  ("gif", "image/gif", FileCategory.IMAGE),
    WEBP ("webp", "image/webp", FileCategory.IMAGE),
    HEIC ("heic", "image/heic", FileCategory.IMAGE),

    // 기본값
    UNKNOWN("", "application/octet-stream", FileCategory.UNKNOWN);

    private final String extension;
    private final String mimeType;
    private final FileCategory category;

    /**
     * 파일 이름으로 ContentType 찾기
     */
    public static FileContentType fromFileName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return FileContentType.UNKNOWN;
        }

        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            String extension = fileName.substring(lastDotIndex + 1).toLowerCase();

            return fromExtension(extension);
        }

        return FileContentType.UNKNOWN;
    }

    /**
     * 파일 확장자로 ContentType 찾기
     */
    private static FileContentType fromExtension(String extension) {
        if (extension == null || extension.isEmpty()) {
            return UNKNOWN;
        }

        String normalizedExt = extension.toLowerCase().trim();

        return Arrays.stream(values())
                .filter(type -> type.extension.equals(normalizedExt))
                .findFirst()
                .orElse(UNKNOWN);
    }

    public boolean isImage() {
        return this.category == FileCategory.IMAGE;
    }

    public boolean isUploadable() {
        return this != UNKNOWN;
    }

    public enum FileCategory {

        IMAGE,
        DOCUMENT,
        VIDEO,
        AUDIO,
        UNKNOWN

    }

}