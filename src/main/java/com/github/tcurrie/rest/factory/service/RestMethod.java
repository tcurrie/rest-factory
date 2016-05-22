package com.github.tcurrie.rest.factory.service;

import com.github.tcurrie.rest.factory.RestParameterAdaptor;
import com.github.tcurrie.rest.factory.RestResponseAdaptor;
import com.github.tcurrie.rest.factory.proxy.MethodImplementation;
import com.openpojo.business.BusinessIdentity;
import com.openpojo.business.annotation.BusinessKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

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
    String invoke(final String request) throws Throwable {
        LOGGER.info("Invoking [{}]", uri);
        final Object[] args = requestAdaptor.apply(request);
        LOGGER.info("Parsed Args [{}]", Arrays.asList(args));
        final U result = implementation.invoke(args);
        LOGGER.info("Got Result [{}]", result);
        return responseAdaptor.apply(result);
    }

    String echo(final String request) {
        final Object[] args = requestAdaptor.apply(request);
        LOGGER.info("Parsed Args [{}]", Arrays.asList(args));
        return ECHO_ADAPTOR.apply(args);
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
