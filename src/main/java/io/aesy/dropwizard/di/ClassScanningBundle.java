package io.aesy.dropwizard.di;

import com.codahale.metrics.health.HealthCheck;
import io.aesy.dropwizard.aop.FilteredMethodInterceptor;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.servlets.tasks.Task;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.hk2.api.messaging.MessageReceiver;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.jvnet.hk2.annotations.Service;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.Scanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.stream.Collectors;

public class ClassScanningBundle<T> implements ConfiguredBundle<T> {
    private static final Logger logger = LoggerFactory.getLogger(ClassScanningBundle.class);

    private final String pckg;
    private final Reflections reflections;

    public ClassScanningBundle(String pckg) {
        Scanner[] scanners = new Scanner[] {
            new MethodAnnotationsScanner(),
            new TypeAnnotationsScanner(),
            new SubTypesScanner()
        };

        this.pckg = pckg;
        this.reflections = new Reflections(pckg, scanners);
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {}

    @Override
    public void run(T configuration, Environment environment) throws Exception {
        logger.info("Scanning for classes to bind in package '" + pckg + "'");

        Set<Class<?>> resources = reflections.getMethodsAnnotatedWith(Path.class)
            .stream()
            .map(Method::getDeclaringClass)
            .collect(Collectors.toSet());
        resources.addAll(reflections.getTypesAnnotatedWith(Path.class));

        Set<Class<? extends HealthCheck>> healthChecks = reflections.getSubTypesOf(HealthCheck.class);
        Set<Class<?>> services = reflections.getTypesAnnotatedWith(Service.class);
        Set<Class<?>> providers = reflections.getTypesAnnotatedWith(Provider.class);
        Set<Class<?>> subscribers = reflections.getTypesAnnotatedWith(MessageReceiver.class);
        Set<Class<? extends Task>> tasks = reflections.getSubTypesOf(Task.class);
        Set<Class<? extends Managed>> managed = reflections.getSubTypesOf(Managed.class);
        Set<Class<? extends FilteredMethodInterceptor>> interceptors = reflections.getSubTypesOf(FilteredMethodInterceptor.class);

        environment.jersey().register(new AbstractBinder() {
            @Override
            protected void configure() {
                logger.info("Found health checks: " + toReadableClassListString(healthChecks));
                healthChecks.forEach(this::addActiveDescriptor);

                logger.info("Found event listeners: " + toReadableClassListString(subscribers));
                subscribers.forEach(this::addActiveDescriptor);

                logger.info("Found admin tasks: " + toReadableClassListString(tasks));
                tasks.forEach(this::addActiveDescriptor);

                // TODO
                logger.info("Found lifecycle listeners: []");

                logger.info("Found managed tasks: " + toReadableClassListString(managed));
                managed.forEach(this::addActiveDescriptor);

                logger.info("Found service classes: " + toReadableClassListString(services));
                services.forEach(this::addActiveDescriptor);

                logger.info("Found method interceptors: " + toReadableClassListString(interceptors));
                interceptors.forEach(this::addActiveDescriptor);
            }
        });

        logger.info("Found provider classes: " + toReadableClassListString(providers));
        providers.forEach(environment.jersey()::register);

        logger.info("Found resource classes: " + toReadableClassListString(resources));
        resources.forEach(environment.jersey()::register);
    }

    private static <T> String  toReadableClassListString(Set<Class<? extends T>> classes) {
        return "[" + classes.stream()
            .map(Class::getName)
            .collect(Collectors.joining(", ")) +
               "]";
    }
}
