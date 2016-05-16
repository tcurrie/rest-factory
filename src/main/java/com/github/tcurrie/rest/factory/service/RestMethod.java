package com.github.tcurrie.rest.factory.service;

import com.github.tcurrie.rest.factory.RestExceptionAdaptor;
import com.github.tcurrie.rest.factory.RestParameterAdaptor;
import com.github.tcurrie.rest.factory.RestResponseAdaptor;
import com.openpojo.business.BusinessIdentity;
import com.openpojo.business.annotation.BusinessKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

final class RestMethod<T, U> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestMethod.class);
    private static final RestResponseAdaptor.Service<Object[]> ECHO_ADAPTOR = RestResponseAdaptor.Service.Factory.create();
    @BusinessKey
    private final String uri;
    private final Method method;
    private final T bean;
    private final RestParameterAdaptor.Service requestAdaptor;
    private final RestResponseAdaptor.Service<U> responseAdaptor;

    RestMethod(final String uri, final Method method, final T bean, final RestParameterAdaptor.Service requestAdaptor, final RestResponseAdaptor.Service<U> responseAdaptor) {
        this.uri = uri;
        this.method = method;
        this.bean = bean;
        this.requestAdaptor = requestAdaptor;
        this.responseAdaptor = responseAdaptor;
    }

    public String getUri() {
        return uri;
    }

    public Method getMethod() {
        return method;
    }

    public T getBean() {
        return bean;
    }

    public RestParameterAdaptor.Service getRequestAdaptor() {
        return requestAdaptor;
    }

    public RestResponseAdaptor.Service<U> getResponseAdaptor() {
        return responseAdaptor;
    }

    @SuppressWarnings("unchecked")
    public void invoke(final HttpServletRequest req, final HttpServletResponse resp) {
        LOGGER.info("Invoking [{}]", uri);
        try {
            final Object[] args = requestAdaptor.apply(req);
            LOGGER.info("Parsed Args [{}]", Arrays.asList(args));
            final U result = (U) method.invoke(bean, args);
            LOGGER.info("Got Result [{}]", result);
            responseAdaptor.apply(result).accept(resp);
        } catch (final InvocationTargetException e) {
            LOGGER.warn("Failed to invoke: {}", uri, e.getCause());
            RestExceptionAdaptor.Service.Factory.apply(e.getCause()).accept(resp);
        } catch (final Exception e) {
            LOGGER.warn("Failed to invoke: {}", uri, e);
            RestExceptionAdaptor.Service.Factory.apply(e).accept(resp);
        }
    }

    public void echo(final HttpServletRequest req, final HttpServletResponse resp) {
        final Object[] args = requestAdaptor.apply(req);
        LOGGER.info("Parsed Args [{}]", Arrays.asList(args));
        ECHO_ADAPTOR.apply(args).accept(resp);
    }

    @Override
    public int hashCode() {
        return BusinessIdentity.getHashCode(this);
    }

    @Override
    public boolean equals(final Object obj) {
        return BusinessIdentity.areEqual(this, obj);
    }

    @Override
    public String toString() {
        return BusinessIdentity.toString(this);
    }

}
