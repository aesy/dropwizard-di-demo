package io.aesy.dropwizard.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.glassfish.jersey.spi.Contract;

import java.lang.reflect.Method;

@Contract
public interface FilteredMethodInterceptor {
    boolean appliesTo(Method method);
    MethodInterceptor getInterceptor();
}
