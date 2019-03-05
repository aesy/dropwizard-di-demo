package io.aesy.dropwizard.convert;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.aesy.dropwizard.dto.MyDto;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
public class MyMessageBodyReader implements MessageBodyReader<MyDto> {
    private final ObjectMapper objectMapper;

    @Inject
    public MyMessageBodyReader(
        ObjectMapper objectMapper
    ) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean isReadable(
        Class<?> type,
        Type genericType,
        Annotation[] annotations,
        MediaType mediaType
    ) {
        return MyDto.class.isAssignableFrom(type);
    }

    @Override
    public MyDto readFrom(
        Class<MyDto> type,
        Type genericType,
        Annotation[] annotations,
        MediaType mediaType,
        MultivaluedMap<String, String> httpHeaders,
        InputStream entityStream
    ) throws IOException, WebApplicationException {
        return objectMapper.readValue(entityStream, MyDto.class);
    }
}
