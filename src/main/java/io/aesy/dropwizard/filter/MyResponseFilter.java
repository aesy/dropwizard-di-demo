package io.aesy.dropwizard.filter;

import org.eclipse.jetty.http.HttpHeader;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.Arrays;

@Provider
public class MyResponseFilter implements ContainerResponseFilter {
    @Override
    public void filter(
        ContainerRequestContext requestContext,
        ContainerResponseContext responseContext
    ) throws IOException {
        responseContext.getHeaders()
            .put(HttpHeader.CACHE_CONTROL.asString(), Arrays.asList("max-age=3600", "no-store"));
    }
}
