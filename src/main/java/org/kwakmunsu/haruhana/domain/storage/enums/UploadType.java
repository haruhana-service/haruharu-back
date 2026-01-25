package org.kwakmunsu.haruhana.domain.storage.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UploadType {

    PROFILE_IMAGE("profile"),
    ;

    private final String directory;

}