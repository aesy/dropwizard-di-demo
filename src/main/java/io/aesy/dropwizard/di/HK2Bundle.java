package io.aesy.dropwizard.di;

import io.aesy.dropwizard.aop.ImperativeMethodInterceptionService;
import io.aesy.dropwizard.aop.UnitOfWorkMethodInterceptor;
import io.aesy.dropwizard.config.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.hk2.extras.events.internal.DefaultTopicDistributionService;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

public class HK2Bundle<T extends Configuration> implements ConfiguredBundle<T> {
    private final String pckg;

    public HK2Bundle(String pckg) {
        this.pckg = pckg;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initialize(Bootstrap<?> bootstrap) {
        Bootstrap<T> b = (Bootstrap<T>) bootstrap;
        b.addBundle(new DropwizardBundle<>());
        b.addBundle(new DatabaseBundle<>(pckg));
        b.addBundle(new ClassScanningBundle<>(pckg));
    }

    @Override
    public void run(T configuration, Environment environment) {
        environment.jersey().register(new AbstractBinder() {
            @Override
            protected void configure() {
                addActiveDescriptor(DefaultTopicDistributionService.class);
                addActiveDescriptor(ImperativeMethodInterceptionService.class);
                addActiveDescriptor(UnitOfWorkMethodInterceptor.class);
            }
        });

        environment.jersey().register(AutoRegisterFeature.class);
    }
}
