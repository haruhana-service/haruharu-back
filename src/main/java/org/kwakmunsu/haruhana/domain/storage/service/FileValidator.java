package org.kwakmunsu.haruhana.domain.storage.service;

import java.util.List;
import org.kwakmunsu.haruhana.domain.storage.enums.FileContentType;
import org.kwakmunsu.haruhana.global.support.error.ErrorType;
import org.kwakmunsu.haruhana.global.support.error.HaruHanaException;
import org.springframework.stereotype.Component;

@Component
public class FileValidator {

    private static final List<String> IMAGE_EXTENSIONS = List.of("jpg", "jpeg", "png", "webp", "heic");

    public void validateFile(String fileName) {
        String extension = getFileExtension(fileName);

        if (!IMAGE_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new HaruHanaException(ErrorType.INVALID_FILE_EXTENSION);
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return FileContentType.UNKNOWN.getExtension();
        }

        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1).toLowerCase();
        }

        return FileContentType.UNKNOWN.getExtension();
    }

}