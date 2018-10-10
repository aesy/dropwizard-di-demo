package io.aesy.dropwizard.dto;

import io.aesy.dropwizard.validation.NotFoo;
import org.hibernate.validator.constraints.NotEmpty;

public class MyDto {
    @NotFoo
    @NotEmpty
    private final String name;

    public MyDto(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
