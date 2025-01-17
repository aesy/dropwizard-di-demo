package io.aesy.dropwizard.managed;

import io.aesy.dropwizard.service.MyService;
import io.dropwizard.lifecycle.Managed;
import org.jvnet.hk2.annotations.ContractsProvided;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@ContractsProvided(Managed.class)
public class MyManaged implements Managed {
    private final MyService myService;

    @Inject
    public MyManaged(
        MyService myService
    ) {
        this.myService = myService;
    }

    @Override
    public void start() throws Exception {
        System.out.println("Start");
        System.out.println(myService.doThing("managed"));
    }

    @Override
    public void stop() throws Exception {
        System.out.println("Stop");
    }
}
