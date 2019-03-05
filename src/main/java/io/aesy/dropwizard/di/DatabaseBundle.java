package io.aesy.dropwizard.di;

import io.aesy.dropwizard.config.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.hibernate.ScanningHibernateBundle;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.hibernate.SessionFactory;

public class DatabaseBundle<T extends Configuration> extends AbstractBinder implements ConfiguredBundle<T> {
    private final MigrationsBundle<T> migrationsBundle;
    private final ScanningHibernateBundle<T> hibernateBundle;

    public DatabaseBundle(String pckg) {
        this.migrationsBundle = new MigrationsBundle<T>() {
            @Override
            public PooledDataSourceFactory getDataSourceFactory(T configuration) {
                return configuration.getDataSourceFactory();
            }
        };

        this.hibernateBundle = new ScanningHibernateBundle<T>(pckg) {
            @Override
            public PooledDataSourceFactory getDataSourceFactory(T configuration) {
                return configuration.getDataSourceFactory();
            }
        };
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initialize(Bootstrap<?> bootstrap) {
        Bootstrap<T> b = (Bootstrap<T>) bootstrap;
        b.addBundle(migrationsBundle);
        b.addBundle(hibernateBundle);
    }

    @Override
    public void run(T configuration, Environment environment) {
        environment.jersey().register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(hibernateBundle.getSessionFactory())
                    .to(SessionFactory.class);
            }
        });
    }

    @Override
    protected void configure() {}
}
