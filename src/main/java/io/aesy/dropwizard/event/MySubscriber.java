package io.aesy.dropwizard.event;

import org.glassfish.hk2.api.messaging.MessageReceiver;
import org.glassfish.hk2.api.messaging.SubscribeTo;

import javax.inject.Singleton;

@MessageReceiver
@Singleton
public class MySubscriber {
    public void testEvent(@SubscribeTo TestEvent event) {
        System.out.println("Subscriber");
    }
}
