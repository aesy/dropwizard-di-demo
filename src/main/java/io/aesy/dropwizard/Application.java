package io.aesy.dropwizard;

import io.aesy.dropwizard.config.Configuration;
import io.aesy.dropwizard.di.HK2Bundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class Application extends io.dropwizard.Application<Configuration> {
    public static void main(String[] args) throws Exception {
        new Application().run(args);
    }

    @Override
    public void initialize(Bootstrap<Configuration> bootstrap) {
        super.initialize(bootstrap);

        bootstrap.addBundle(new HK2Bundle<>("io.aesy"));
    }

    @Override
    public void run(Configuration configuration, Environment environment) {}
}
