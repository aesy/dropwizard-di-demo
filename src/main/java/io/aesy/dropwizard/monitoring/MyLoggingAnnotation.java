package io.aesy.dropwizard.monitoring;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({METHOD, CONSTRUCTOR, TYPE})
public @interface MyLoggingAnnotation {}
