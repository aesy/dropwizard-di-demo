package io.aesy.dropwizard.resource;

import com.codahale.metrics.annotation.Timed;
import io.aesy.dropwizard.dto.MyDto;
import io.aesy.dropwizard.entity.MyEntity;
import io.aesy.dropwizard.event.MyEvent;
import io.aesy.dropwizard.service.MyService;
import org.glassfish.hk2.api.messaging.Topic;
import org.glassfish.jersey.process.internal.RequestScoped;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.List;

@Path("/")
@RequestScoped
public class MyResource {
    private final Topic<MyEvent> testEvent;
    private final MyService myService;

    @Inject
    public MyResource(
        Topic<MyEvent> testEvent,
        MyService myService
    ) {
        this.testEvent = testEvent;
        this.myService = myService;
    }

    @GET
    @Path("/events")
    public String events() {
        testEvent.publish(new MyEvent("evennnnttt"));

        return "woop";
    }

    @GET
    @Path("/metrics")
    @Timed
    public String metrics() {
        return myService.doThing("noop");
    }

    @GET
    @Path("/interceptions")
    public String interception() {
        return "woop";
    }

    @GET
    @Path("/repositories")
    public List<MyEntity> repositories() {
        return myService.getThing();
    }

    @POST
    @Path("/validations")
    public MyDto validations(@Valid MyDto dto) {
        return dto;
    }
}
