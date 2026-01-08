package org.kwakmunsu.haruhana.global.support.error;

import lombok.Getter;

@Getter
public class HaruHanaException extends RuntimeException {

    private final ErrorType errorType;
    private final Object data;

    public HaruHanaException(ErrorType type) {
        super(type.getMessage());
        this.errorType = type;
        this.data = null;
    }

    public HaruHanaException(ErrorType type, Object data) {
        super(type.getMessage());
        this.errorType = type;
        this.data = data;
    }

}