package io.aesy.dropwizard.monitoring;

import io.aesy.dropwizard.aop.FilteredMethodInterceptor;
import org.aopalliance.intercept.MethodInterceptor;
import org.glassfish.hk2.api.Rank;
import org.jvnet.hk2.annotations.ContractsProvided;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

@Rank(5)
@ContractsProvided(FilteredMethodInterceptor.class)
public class MyInvocationLoggerInterceptor implements FilteredMethodInterceptor {
    @Override
    public boolean appliesTo(Method method) {
        return method.isAnnotationPresent(MyLoggingAnnotation.class);
    }

    @Override
    public MethodInterceptor getInterceptor() {
        return invocation -> {
            System.out.println(invocation.getMethod().toGenericString());
            System.out.println("With arguments: " +
                    Arrays.stream(invocation.getArguments())
                            .map(Object::toString)
                            .collect(Collectors.joining(", ")));

            return invocation.proceed();
        };
    }
}
