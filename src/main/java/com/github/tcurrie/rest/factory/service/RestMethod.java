package com.github.tcurrie.rest.factory.service;

import com.github.tcurrie.rest.factory.RestParameterAdaptor;
import com.openpojo.business.BusinessIdentity;
import com.openpojo.business.annotation.BusinessKey;
import com.github.tcurrie.rest.factory.RestResponseAdaptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

final class RestMethod<T, U> {
    private static final Logger LOGGER = Logger.getLogger(RestMethod.class.getName());
    private static final RestResponseAdaptor<Object[]> ECHO_ADAPTOR = RestResponseAdaptor.Factory.<Object[]>create();
    @BusinessKey
    private final String uri;
    private final Method method;
    private final T bean;
    private final RestParameterAdaptor.Service requestAdaptor;
    private final RestResponseAdaptor<U> responseAdaptor;

    RestMethod(final String uri, final Method method, final T bean, final RestParameterAdaptor.Service requestAdaptor, final RestResponseAdaptor<U> responseAdaptor) {
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

    @SuppressWarnings("unchecked")
    public void invoke(final HttpServletRequest req, final HttpServletResponse resp) {
        LOGGER.log(Level.INFO, "Invoking [{0}]", this);
        try {
            final Object[] args = requestAdaptor.apply(req);
            LOGGER.log(Level.INFO, "Parsed Args [{0}]", Arrays.asList(args));
            final U result = (U) method.invoke(bean, args);
            LOGGER.log(Level.INFO, "Got Result [{0}]", result);
            responseAdaptor.apply(result).accept(resp);
        } catch (InvocationTargetException e) {
            LOGGER.log(Level.SEVERE, "Failed to invoke [{0}]: {1}", new Object[]{e, e.getCause()});
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to invoke [{0}]", e);
        }
    }

    public void echo(final HttpServletRequest req, final HttpServletResponse resp) {
        final Object[] args = requestAdaptor.apply(req);
        LOGGER.log(Level.INFO, "Parsed Args [{0}]", Arrays.asList(args));
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
