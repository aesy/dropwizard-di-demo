package io.aesy.dropwizard.resource;

import com.codahale.metrics.annotation.Timed;
import io.aesy.dropwizard.aop.Log;
import io.aesy.dropwizard.event.TestEvent;
import io.aesy.dropwizard.service.MyService;
import org.glassfish.hk2.api.messaging.Topic;
import org.glassfish.hk2.extras.interception.Intercepted;
import org.glassfish.jersey.process.internal.RequestScoped;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
@Intercepted
@RequestScoped
public class MyResource {
    private final Topic<TestEvent> testEvent;
    private final MyService myService;

    @Inject
    public MyResource(
        Topic<TestEvent> testEvent,
        MyService myService
    ) {
        this.testEvent = testEvent;
        this.myService = myService;
    }

    @GET
    @Timed
    @Log
    public String resourceTest() {
        testEvent.publish(new TestEvent());
        myService.doThing("noop");

        return "Hello World";
    }

}
