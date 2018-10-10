package io.aesy.dropwizard.service;

import com.codahale.metrics.MetricRegistry;
import io.aesy.dropwizard.aop.Log;
import io.aesy.dropwizard.aop.Log2;
import org.glassfish.hk2.extras.interception.Intercepted;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;

@Intercepted
@Service
public class MyService {
    private final MetricRegistry metrics;

    @Inject
    public MyService(
        MetricRegistry metrics
    ) {
        this.metrics = metrics;
    }

    @Log
    @Log2
    public void doThing(String say) {
        metrics.counter("test").inc();

        System.out.println(say);
    }
}
