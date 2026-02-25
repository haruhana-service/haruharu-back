package org.kwakmunsu.haruhana.global.support;

public record OffsetLimit(
        int page,
        int size
) {
    public OffsetLimit {
        if (page < 1) page = 1;
        if (size < 1) size = 1;
        if (size > 30) size = 30;
    }

    public long offset() {
        return (long) (page - 1) * size;
    }

    public long limit() {
        return size;
    }

}