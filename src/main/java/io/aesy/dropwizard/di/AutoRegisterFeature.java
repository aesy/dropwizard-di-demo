package io.aesy.dropwizard.di;

import com.codahale.metrics.health.HealthCheck;
import io.aesy.dropwizard.aop.FilteredMethodInterceptor;
import io.dropwizard.lifecycle.JettyManaged;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.servlets.tasks.Task;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.util.component.LifeCycle;
import org.glassfish.hk2.api.IterableProvider;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.messaging.MessageReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class AutoRegisterFeature implements Feature {
    private static final Logger logger = LoggerFactory.getLogger(AutoRegisterFeature.class);

    @Inject
    private Environment environment;

    @Inject
    private ServiceLocator locator;

    @Inject
    private IterableProvider<HealthCheck> healthCheckProvider;

    @Inject
    private IterableProvider<Task> tasksProvider;

    @Inject
    private IterableProvider<Managed> managedProvider;

    @Inject
    private IterableProvider<LifeCycle> lifeCyclesProvider;

    @Inject
    private IterableProvider<FilteredMethodInterceptor> methodInterceptors;

    @Override
    public boolean configure(FeatureContext context) {
        logger.info("Registering bound classes");

        Set<Class<?>> healthChecks = StreamSupport.stream(healthCheckProvider.spliterator(), true)
            .map(HealthCheck::getClass)
            .collect(Collectors.toSet());
        logger.info("Registered health checks: " + toReadableClassListString(healthChecks));

        healthCheckProvider.forEach(healthCheck -> {
            String name;
            if (healthCheck.getClass().isAnnotationPresent(Named.class)) {
                name = healthCheck.getClass().getAnnotation(Named.class).value();
            } else {
                name = healthCheck.getClass().getName();
            }
            environment.healthChecks().register(name, healthCheck);
        });

        List<ServiceHandle<MessageReceiver>> subscribers = locator.getAllServiceHandles(MessageReceiver.class);
        Set<Class<?>> collect = subscribers.parallelStream()
            .map(o -> o.getActiveDescriptor().getImplementationClass())
            .collect(Collectors.toSet());
        logger.info("Registered event listeners: " + toReadableClassListString(collect));
        subscribers.forEach(ServiceHandle::getService);

        Set<Class<?>> tasks = StreamSupport.stream(tasksProvider.spliterator(), true)
            .map(Task::getClass)
            .collect(Collectors.toSet());
        logger.info("Registered admin tasks: " + toReadableClassListString(tasks));
        tasksProvider.forEach(environment.admin()::addTask);

        Set<Class<?>> lifecycle = StreamSupport.stream(lifeCyclesProvider.spliterator(), true)
            .map(LifeCycle::getClass)
            .collect(Collectors.toSet());
        logger.info("Registered lifecycle listeners: " + toReadableClassListString(lifecycle));
        lifeCyclesProvider.forEach(environment.getApplicationContext()::addManaged);

        Set<Class<?>> managed = StreamSupport.stream(managedProvider.spliterator(), true)
            .map(Managed::getClass)
            .collect(Collectors.toSet());
        logger.info("Registered managed tasks: " + toReadableClassListString(managed));
        StreamSupport.stream(managedProvider.spliterator(), false)
                     .map(JettyManaged::new)
                     .forEach(environment.getApplicationContext()::addManaged);

        Set<Class<?>> interceptors = StreamSupport.stream(methodInterceptors.spliterator(), true)
            .map(FilteredMethodInterceptor::getClass)
            .collect(Collectors.toSet());
        logger.info("Registered method interceptors: " + toReadableClassListString(interceptors));

        return true;
    }

    private static <T> String  toReadableClassListString(Set<Class<? extends T>> classes) {
        return "[" + classes.stream()
            .map(Class::getName)
            .collect(Collectors.joining(", ")) +
               "]";
    }
}
