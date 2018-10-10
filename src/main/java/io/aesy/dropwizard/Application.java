package io.aesy.dropwizard;

import io.aesy.dropwizard.aop.MyInterceptor;
import io.aesy.dropwizard.aop.MyInterceptor2;
import io.aesy.dropwizard.di.HK2Bundle;
import io.aesy.dropwizard.event.MySubscriber;
import io.aesy.dropwizard.health.MyHealthCheck;
import io.aesy.dropwizard.job.MyManaged;
import io.aesy.dropwizard.resource.MyResource;
import io.aesy.dropwizard.service.MyService;
import io.aesy.dropwizard.task.MyTask;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

public class Application extends io.dropwizard.Application<Configuration> {
    public static void main(String[] args) throws Exception {
        new Application().run(args);
    }

    @Override
    public void initialize(Bootstrap<Configuration> bootstrap) {
        super.initialize(bootstrap);

        bootstrap.addBundle(new HK2Bundle<>(true));
    }

    @Override
    public void run(Configuration configuration, Environment environment) {
        environment.jersey().register(new AbstractBinder() {
            @Override
            protected void configure() {
                addActiveDescriptor(MyManaged.class);
                addActiveDescriptor(MyHealthCheck.class);
                addActiveDescriptor(MyService.class);
                addActiveDescriptor(MyInterceptor.class);
                addActiveDescriptor(MyInterceptor2.class);
                addActiveDescriptor(MySubscriber.class);
                addActiveDescriptor(MyResource.class);
                addActiveDescriptor(MyTask.class);
            }
        });

        environment.jersey().register(MyResource.class);
    }
}
