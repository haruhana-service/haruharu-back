package org.kwakmunsu.haruhana.global.swagger;

import io.swagger.v3.oas.models.examples.Example;
import lombok.Builder;

@Builder
public record ExampleHolder(
        Example holder,
        String name,
        int code
) {

}