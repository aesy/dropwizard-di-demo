package io.aesy.dropwizard.validation;

import io.aesy.dropwizard.service.IsFooService;

import javax.inject.Inject;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NotFooValidator implements ConstraintValidator<NotFoo, String> {
    private final IsFooService isFooService;

    @Inject
    public NotFooValidator(IsFooService isFooService) {
        this.isFooService = isFooService;
    }

    @Override
    public void initialize(NotFoo annotation) {}

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return !isFooService.isFoo(value);
    }
}
