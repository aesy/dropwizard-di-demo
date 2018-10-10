package io.aesy.dropwizard.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = { NotFooValidator.class })
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(value = RetentionPolicy.RUNTIME)
public @interface NotFoo {
    boolean caseSensitive() default false;
    String message() default "Must not be foo";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
