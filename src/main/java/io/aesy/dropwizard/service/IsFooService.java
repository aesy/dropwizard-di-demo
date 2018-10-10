package io.aesy.dropwizard.service;

import org.jvnet.hk2.annotations.Service;

@Service
public class IsFooService {
    public boolean isFoo(String value) {
        if (value == null) {
            return false;
        }

        return value.toLowerCase().equals("foo");
    }
}
