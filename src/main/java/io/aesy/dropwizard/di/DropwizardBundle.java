package io.aesy.dropwizard.di;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.validation.Validator;

public class DropwizardBundle<T extends Configuration> implements ConfiguredBundle<T> {
    private Bootstrap<T> bootstrap;

    @SuppressWarnings("unchecked")
    @Override
    public void initialize(Bootstrap<?> bootstrap) {
        this.bootstrap = (Bootstrap<T>) bootstrap;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void run(T configuration, Environment environment) throws Exception {
        environment.jersey().register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(environment)
                    .to(Environment.class);

                bind(environment.healthChecks())
                    .to(HealthCheckRegistry.class);

                bind(environment.lifecycle())
                    .to(LifecycleEnvironment.class);

                bind(environment.metrics())
                    .to(MetricRegistry.class);

                bind(environment.getValidator())
                    .to(Validator.class);

                bind(configuration)
                    .to(bootstrap.getApplication().getConfigurationClass())
                    .to(Configuration.class);

                bind(environment.getObjectMapper())
                    .to(ObjectMapper.class);

                bind(bootstrap.getApplication())
                    .to((Class) bootstrap.getApplication().getClass())
                    .to(Application.class);
            }
        });
    }
}
