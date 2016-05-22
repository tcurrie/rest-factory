package com.github.tcurrie.rest.factory.client;

import com.github.tcurrie.rest.factory.RestParameterAdaptor;
import com.github.tcurrie.rest.factory.RestResponseAdaptor;
import com.github.tcurrie.rest.factory.RestUri;
import com.github.tcurrie.rest.factory.proxy.ProxyMethod;
import com.openpojo.business.BusinessIdentity;
import com.openpojo.business.annotation.BusinessKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import static com.github.tcurrie.rest.factory.client.HTTPExchange.Method.POST;

//TODO Remove dependency on Spring's rest template and/or at least handle timeouts!
class RestClientMethod<T> implements ProxyMethod<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestClientMethod.class);
    private final Method method;
    @BusinessKey private final RestUri methodUrlSupplier;
    private final RestParameterAdaptor.Client parameterAdaptor;
    private final RestResponseAdaptor.Client<T> resultAdaptor;

    RestClientMethod(final Method method, final RestUri methodUrlSupplier, final RestParameterAdaptor.Client parameterAdaptor, final RestResponseAdaptor.Client<T> resultAdaptor) {
        this.method = method;
        this.methodUrlSupplier = methodUrlSupplier;
        this.parameterAdaptor = parameterAdaptor;
        this.resultAdaptor = resultAdaptor;
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @SuppressWarnings("unused")
    RestUri getMethodUrlSupplier() {
        return methodUrlSupplier;
    }

    @SuppressWarnings("unused")
    RestParameterAdaptor.Client getParameterAdaptor() {
        return parameterAdaptor;
    }

    @SuppressWarnings("unused")
    public RestResponseAdaptor.Client<T> getResultAdaptor() {
        return resultAdaptor;
    }

    @Override
    public T invoke(final Object[] args) throws Throwable {
        final String url = methodUrlSupplier.get();
        final String body = parameterAdaptor.apply(args);
        LOGGER.info("For method [{}] and args [{}], posting to [{}] with [{}]", method, args, url, body);

        final String response = HTTPExchange.execute(url, body, POST, 30, TimeUnit.SECONDS);

        return resultAdaptor.apply(response);
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
