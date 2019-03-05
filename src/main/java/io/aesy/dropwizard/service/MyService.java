package io.aesy.dropwizard.service;

import com.codahale.metrics.MetricRegistry;
import io.aesy.dropwizard.entity.MyEntity;
import io.aesy.dropwizard.monitoring.MyLoggingAnnotation;
import io.aesy.dropwizard.repository.MyEntityRepository;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.util.List;

@Service
public class MyService {
    private final MetricRegistry metrics;
    private final MyEntityRepository repository;

    @Inject
    public MyService(
        MetricRegistry metrics,
        MyEntityRepository repository
    ) {
        this.metrics = metrics;
        this.repository = repository;
    }

    @MyLoggingAnnotation
    public String doThing(String say) {
        metrics.counter("test").inc();
        return "woop" + say;
    }

    public List<MyEntity> getThing() {
        return repository.findAll();
    }

    public void addThing(MyEntity entity) {
        repository.create(entity);
    }
}
