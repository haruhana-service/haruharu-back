package org.kwakmunsu.haruhana.global.support.response;

import org.kwakmunsu.haruhana.global.support.error.ErrorType;

public record ErrorMessage(
        String code,
        String message,
        Object data
) {

    public ErrorMessage(ErrorType errorType) {
        this(errorType.getStatus().name(), errorType.getMessage(), null);
    }

    public ErrorMessage(ErrorType errorType, Object data) {
        this(errorType.getStatus().name(), errorType.getMessage(), data);
    }

}