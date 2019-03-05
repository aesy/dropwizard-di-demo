package io.aesy.dropwizard.repository;

import io.aesy.dropwizard.entity.MyEntity;
import io.dropwizard.hibernate.UnitOfWork;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jvnet.hk2.annotations.ContractsProvided;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Singleton
@ContractsProvided(MyEntityRepository.class)
@Service
public class MyEntityRepository {
    private final SessionFactory sessionFactory;

    @Inject
    public MyEntityRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @UnitOfWork
    public List<MyEntity> findAll() {
        try (Session session = sessionFactory.getCurrentSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<MyEntity> query = builder.createQuery(MyEntity.class);
            Root<MyEntity> root = query.from(MyEntity.class);
            query.select(root);

            return session.createQuery(query)
                          .getResultList();
        }
    }

    @UnitOfWork
    public void create(MyEntity entity) {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.persist(entity);
        }
    }
}
