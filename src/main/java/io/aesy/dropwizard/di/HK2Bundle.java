package io.aesy.dropwizard.di;

import io.aesy.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.hibernate.ScanningHibernateBundle;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.hk2.extras.events.internal.DefaultTopicDistributionService;
import org.glassfish.hk2.extras.interception.internal.DefaultInterceptionService;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.hibernate.SessionFactory;

public class DatabaseBundle<T extends Configuration> extends AbstractBinder implements ConfiguredBundle<T> {
    private final MigrationsBundle<T> migrationsBundle;
    private final HibernateBundle<T> hibernateBundle;

    public DatabaseBundle(String pckg) {

    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
        bootstrap.addBundle(migrationsBundle);
        bootstrap.addBundle(hibernateBundle);
    }

    @Override
    public void run(T configuration, Environment environment) {}

    @Override
    protected void configure() {

    }
}

public class HK2Bundle<T extends Configuration> implements ConfiguredBundle<T> {
    private final boolean autoRegister;
    private Bootstrap<?> bootstrap;
    private HibernateBundle<T> hibernateBundle;
    private MigrationsBundle<T> migrationsBundle;

    public HK2Bundle(boolean autoRegister) {
        this.autoRegister = autoRegister;
    }

    @Override
    public void run(T configuration, Environment environment) {
        environment.jersey().register(new DatabaseBundle(""));
        environment.jersey().register(new DropwizardBinder<>(configuration, environment, bootstrap));
        environment.jersey().register(new AbstractBinder() {
            @Override
            protected void configure() {
                addActiveDescriptor(DefaultInterceptionService.class);
                addActiveDescriptor(DefaultTopicDistributionService.class);
            }
        });

        environment.jersey().register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(hibernateBundle.getSessionFactory())
                    .to(SessionFactory.class);
            }
        });

        if (autoRegister) {
            environment.jersey().register(AutoRegisterFeature.class);
        }
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
        this.bootstrap = bootstrap;

        this.migrationsBundle = new MigrationsBundle<T>() {
            @Override
            public PooledDataSourceFactory getDataSourceFactory(T configuration) {
                return configuration.getDataSourceFactory();
            }
        };

        this.hibernateBundle = new ScanningHibernateBundle<T>("pckg") {
            @Override
            public PooledDataSourceFactory getDataSourceFactory(T configuration) {
                return configuration.getDataSourceFactory();
            }
        };

        bootstrap.addBundle(migrationsBundle);
    }
}
