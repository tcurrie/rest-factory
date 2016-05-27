package com.github.tcurrie.rest.factory.client;

import com.github.tcurrie.rest.factory.EchoResponseAdaptor;
import com.github.tcurrie.rest.factory.RestParameterAdaptor;
import com.github.tcurrie.rest.factory.RestResponseAdaptor;
import com.github.tcurrie.rest.factory.RestUri;
import com.github.tcurrie.rest.factory.proxy.ProxyMethod;
import com.github.tcurrie.rest.factory.v1.RestMethodVerificationResult;
import com.github.tcurrie.rest.factory.v1.TimeOut;
import com.openpojo.business.BusinessIdentity;
import com.openpojo.business.annotation.BusinessKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.function.Supplier;

import static com.github.tcurrie.rest.factory.client.HTTPExchange.Method.ECHO;
import static com.github.tcurrie.rest.factory.client.HTTPExchange.Method.POST;

//TODO Remove dependency on Spring's rest template and/or at least handle timeouts!
class RestClientMethod<T> implements ProxyMethod<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestClientMethod.class);
    @BusinessKey private final Method method;
    private final RestUri methodUrlSupplier;
    private final Supplier<TimeOut> timeOutSupplier;
    private final RestParameterAdaptor.Client parameterAdaptor;
    private final RestResponseAdaptor.Client<T> resultAdaptor;
    private final EchoResponseAdaptor.Client echoAdaptor;

    RestClientMethod(final Method method, final RestUri methodUrlSupplier, final Supplier<TimeOut> timeOutSupplier, final RestParameterAdaptor.Client parameterAdaptor, final RestResponseAdaptor.Client<T> resultAdaptor, final EchoResponseAdaptor.Client echoAdaptor) {
        this.method = method;
        this.methodUrlSupplier = methodUrlSupplier;
        this.timeOutSupplier = timeOutSupplier;
        this.parameterAdaptor = parameterAdaptor;
        this.resultAdaptor = resultAdaptor;
        this.echoAdaptor = echoAdaptor;
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

    @SuppressWarnings("unused")
    public EchoResponseAdaptor.Client getEchoAdaptor() {
        return echoAdaptor;
    }

    @Override
    public T invoke(final Object[] args) throws Throwable {
        final String url = methodUrlSupplier.get();
        final TimeOut timeout = timeOutSupplier.get();
        final String body = parameterAdaptor.apply(args);
        LOGGER.debug("For method [{}] and args [{}], posting to [{}] with [{}] within [{}].", method, args, url, body, timeout);

        final String response = HTTPExchange.execute(url, body, POST, timeout);

        return resultAdaptor.apply(response);
    }


    @SuppressWarnings("WeakerAccess")
    public RestMethodVerificationResult echo(final Object[] args) {
        final String url = methodUrlSupplier.get();
        final TimeOut timeout = timeOutSupplier.get();
        final String body = parameterAdaptor.apply(args);
        LOGGER.debug("For method [{}] and args [{}], echoing to [{}] with [{}] within [{}].", method, args, url, body, timeout);
        try {
            final String response = HTTPExchange.execute(url, body, ECHO, timeout);
            return echoAdaptor.apply(url, args, response);
        } catch (final Throwable t) {
            return RestMethodVerificationResult.createFailure(url, method.toGenericString(), args, t);
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
