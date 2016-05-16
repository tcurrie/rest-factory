package com.github.tcurrie.rest.factory.client;

import com.github.tcurrie.rest.factory.RestParameterAdaptor;
import com.github.tcurrie.rest.factory.RestResponseAdaptor;
import com.github.tcurrie.rest.factory.proxy.ProxyMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Method;
import java.util.function.Supplier;

//TODO Remove dependency on Spring's rest template and/or at least handle timeouts!
class RestClientMethod<T> implements ProxyMethod<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestClientMethod.class);
    private final Method method;
    private final Supplier<String> methodUrlSupplier;
    private final RestParameterAdaptor.Client parameterAdaptor;
    private final RestResponseAdaptor.Client<T> resultAdaptor;

    RestClientMethod(final Method method, final Supplier<String> methodUrlSupplier, final RestParameterAdaptor.Client parameterAdaptor, final RestResponseAdaptor.Client<T> resultAdaptor) {
        this.method = method;
        this.methodUrlSupplier = methodUrlSupplier;
        this.parameterAdaptor = parameterAdaptor;
        this.resultAdaptor = resultAdaptor;
    }

    @Override
    public Method getMethod() {
        return method;
    }

    Supplier<String> getMethodUrlSupplier() {
        return methodUrlSupplier;
    }

    RestParameterAdaptor.Client getParameterAdaptor() {
        return parameterAdaptor;
    }


    @Override
    public T invoke(final Object[] args) throws Throwable {
        final String url = methodUrlSupplier.get();
        final String body = parameterAdaptor.apply(args);
        LOGGER.info("For method [{}] and args [{}], posting to [{}] with [{}]", method, args, url, body);

        final ResponseEntity<String> response = new RestTemplate().exchange(url, HttpMethod.POST, new HttpEntity<>(body), String.class);

        return resultAdaptor.apply(response.getBody());
    }
}
