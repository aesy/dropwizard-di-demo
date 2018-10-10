package io.aesy.dropwizard.repository;

import io.aesy.dropwizard.entity.MyEntity;
import io.dropwizard.hibernate.UnitOfWork;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

@Singleton
public class MyEntityRepository {
    private final SessionFactory sessionFactory;

    @Inject
    public MyEntityRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @UnitOfWork
    public List<MyEntity> findAll() {
        try (Session session = sessionFactory.getCurrentSession()) {
            CriteriaQuery<MyEntity> query = session.getCriteriaBuilder()
                                                   .createQuery(MyEntity.class);

            return session.createQuery(query)
                          .getResultList();
        }
    }
}
