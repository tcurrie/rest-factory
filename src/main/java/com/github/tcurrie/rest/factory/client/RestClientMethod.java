package com.github.tcurrie.rest.factory.client;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tcurrie.rest.factory.ResponseWrapper;
import com.github.tcurrie.rest.factory.RestParameterAdaptor;
import com.github.tcurrie.rest.factory.proxy.ProxyMethod;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

class RestClientMethod<T> implements ProxyMethod<T> {
    private static final Logger LOGGER = Logger.getLogger(RestClientMethod.class.getName());
    private final Method method;
    private final Supplier<String> methodUrlSupplier;
    private final RestParameterAdaptor.Client parameterAdaptor;
    private final Class<T> methodResult;

    RestClientMethod(final Method method, final Supplier<String> methodUrlSupplier, final RestParameterAdaptor.Client parameterAdaptor, final Class<T> methodResult) {
        this.method = method;
        this.methodUrlSupplier = methodUrlSupplier;
        this.parameterAdaptor = parameterAdaptor;
        this.methodResult = methodResult;
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

    Class<T> getMethodResult() {
        return methodResult;
    }

    @Override
    public T invoke(final Object[] args) {
        final String url = methodUrlSupplier.get();
        final String body = parameterAdaptor.apply(args);
        LOGGER.log(Level.INFO, "For method [{0}] and args [{1}], posting to [{2}] with [{3}]", new Object[]{method, args, url, body});
        //TODO Remove dependency on Spring's rest template and/or at least handle timeouts!
        final RestTemplate template = new RestTemplate();
        final HttpEntity<String> entity = new HttpEntity<>(body);
        final ResponseEntity<String> response = template.exchange(url, HttpMethod.POST, entity, String.class);
        System.out.println(response.getBody());


        final JavaType type = new ObjectMapper().getTypeFactory().constructParametrizedType(ResponseWrapper.class, ResponseWrapper.class,
                void.class.equals(methodResult) ? Object.class : methodResult);
        System.out.println(type);
        try {
            final ResponseWrapper<T> wrapper = new ObjectMapper().readValue(response.getBody(), type);
            return wrapper.getResult();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }
}
