package io.aesy.dropwizard.event;

import io.aesy.dropwizard.service.MyService;
import org.glassfish.hk2.api.messaging.MessageReceiver;
import org.glassfish.hk2.api.messaging.SubscribeTo;

import javax.inject.Inject;
import javax.inject.Singleton;

@MessageReceiver
@Singleton
public class MySubscriber {
    private final MyService myService;

    @Inject
    public MySubscriber(MyService myService) {
        this.myService = myService;
    }

    public void testEvent(@SubscribeTo MyEvent event) {
        System.out.println("Message recieved: " + event.getPayload());
        myService.doThing("Subscriber to service!");
    }
}
