package io.aesy.dropwizard.di;

import io.dropwizard.hibernate.HibernateBundle;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.hibernate.SessionFactory;

public class HibernateBinder extends AbstractBinder {
    private final HibernateBundle bundle;

    public HibernateBinder(HibernateBundle bundle) {
        this.bundle = bundle;
    }

    @Override
    protected void configure() {
        SessionFactory sessionFactory = bundle.getSessionFactory();

        bind(sessionFactory)
            .to(SessionFactory.class);
    }
}
