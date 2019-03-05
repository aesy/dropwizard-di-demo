package io.aesy.dropwizard.convert;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.aesy.dropwizard.dto.MyDto;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
public class MyMessageBodyWriter implements MessageBodyWriter<MyDto>  {
    private final ObjectMapper objectMapper;

    @Inject
    public MyMessageBodyWriter(
        ObjectMapper objectMapper
    ) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean isWriteable(
        Class<?> type,
        Type genericType,
        Annotation[] annotations,
        MediaType mediaType
    ) {
        return MyDto.class.isAssignableFrom(type);
    }

    @Override
    public long getSize(
        MyDto myDto,
        Class<?> type,
        Type genericType,
        Annotation[] annotations,
        MediaType mediaType
    ) {
        return -1;
    }

    @Override
    public void writeTo(
        MyDto myDto,
        Class<?> type,
        Type genericType,
        Annotation[] annotations,
        MediaType mediaType,
        MultivaluedMap<String, Object> httpHeaders,
        OutputStream entityStream
    ) throws IOException, WebApplicationException {
        String response = objectMapper.writeValueAsString(myDto);

        entityStream.write(response.getBytes());
    }
}
