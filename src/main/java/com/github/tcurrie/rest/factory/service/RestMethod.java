package com.github.tcurrie.rest.factory.service;

import com.github.tcurrie.rest.factory.RestParameterAdaptor;
import com.github.tcurrie.rest.factory.RestResponseAdaptor;
import com.github.tcurrie.rest.factory.Strings;
import com.github.tcurrie.rest.factory.proxy.MethodImplementation;
import com.github.tcurrie.rest.factory.v1.RestFactoryException;
import com.openpojo.business.BusinessIdentity;
import com.openpojo.business.annotation.BusinessKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

final class RestMethod<T, U> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestMethod.class);
    private static final RestResponseAdaptor.Service<Object[]> ECHO_ADAPTOR = RestResponseAdaptor.Service.Factory.create();

    @BusinessKey
    private final String uri;
    private final MethodImplementation<T, U> implementation;
    private final RestParameterAdaptor.Service requestAdaptor;
    private final RestResponseAdaptor.Service<U> responseAdaptor;

    RestMethod(final String uri, final MethodImplementation<T, U> implementation, final RestParameterAdaptor.Service requestAdaptor, final RestResponseAdaptor.Service<U> responseAdaptor) {
        this.uri = uri;
        this.implementation = implementation;
        this.requestAdaptor = requestAdaptor;
        this.responseAdaptor = responseAdaptor;
    }

    String getUri() {
        return uri;
    }

    MethodImplementation getImplementation() {
        return implementation;
    }

    @SuppressWarnings("unused")
    public RestParameterAdaptor.Service getRequestAdaptor() {
        return requestAdaptor;
    }

    @SuppressWarnings("unused")
    public RestResponseAdaptor.Service<U> getResponseAdaptor() {
        return responseAdaptor;
    }

    @SuppressWarnings("unchecked")
    void invoke(final HttpServletRequest request, final HttpServletResponse response) {
        LOGGER.info("Invoking [{}]", uri);
        try {
            final Object[] args = requestAdaptor.apply(getBody(request));
            LOGGER.info("Parsed Args [{}]", Arrays.asList(args));
            final U result = implementation.invoke(args);
            LOGGER.info("Got Result [{}]", result);

            final String json = responseAdaptor.apply(result);

            writeResponse(response, json, HttpServletResponse.SC_OK);
        } catch (final Throwable e) {
            writeException(response, e);
        }
    }

    void echo(final HttpServletRequest request, final HttpServletResponse response) {
        try {
            final Object[] args = requestAdaptor.apply(getBody(request));
            LOGGER.info("Parsed Args [{}]", Arrays.asList(args));
            final String json = ECHO_ADAPTOR.apply(args);
            writeResponse(response, json, HttpServletResponse.SC_OK);
        } catch (final Exception e) {
            writeException(response, e);
        }
    }

    private void writeException(final HttpServletResponse response, final Throwable t) {
        LOGGER.warn("Failed to invoke: {}", uri, t);
        final String json = RestResponseAdaptor.Service.THROWABLE.apply(t);
        writeResponse(response, json, HttpServletResponse.SC_OK);
    }

    private String getBody(final HttpServletRequest req) throws IOException {
        return req.getReader().lines().collect(Collectors.joining("\n"));
    }

    void writeResponse(final HttpServletResponse resp, final String result, final int status)  {
        resp.setStatus(status);
        resp.setHeader("Content-Type", "application/json");
        try {
            resp.getWriter().write(result);
        } catch (final IOException e) {
            throw RestFactoryException.create(Strings.format("Failed to write response [{}] with status [{}].", result, status), e);
        }
    }

    @Override
    public int hashCode() {
        return BusinessIdentity.getHashCode(this);
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(final Object obj) {
        return BusinessIdentity.areEqual(this, obj);
    }

    @Override
    public String toString() {
        return BusinessIdentity.toString(this);
    }

}
