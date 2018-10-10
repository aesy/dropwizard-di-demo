package io.aesy.dropwizard.di;

import io.aesy.dropwizard.Configuration;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.hibernate.ScanningHibernateBundle;

public class HibernateBundle<T extends Configuration> extends ScanningHibernateBundle<T> {
    public HibernateBundle(String pckg) {
        super(pckg);
    }

    @Override
    public PooledDataSourceFactory getDataSourceFactory(T configuration) {
        return configuration.getDataSourceFactory();
    }
}
