package com.github.tcurrie.rest.factory.client;

import com.github.tcurrie.rest.factory.RestParameterAdaptor;
import com.github.tcurrie.rest.factory.RestResponseAdaptor;
import com.github.tcurrie.rest.factory.RestUriFactory;
import com.github.tcurrie.rest.factory.model.RestFactoryException;
import com.github.tcurrie.rest.factory.proxy.ProxyFactory;

import java.lang.reflect.Method;
import java.util.function.Supplier;
import java.util.logging.Logger;

public final class RestClientFactory {
    private RestClientFactory() {
        throw RestFactoryException.create("Can not construct instance of Factory class.");
    }

    private static final Logger LOGGER = Logger.getLogger(RestClientFactory.class.getName());

    public static <T> T create(final Class<T> service, final Supplier<String> urlSupplier) {
        return ProxyFactory.create(service, method -> create(service, urlSupplier, method));
    }

    static <T, U> RestClientMethod<U> create(final Class<T> service, final Supplier<String> urlSupplier, final Method method) {
        final Supplier<String> methodUrlSupplier = RestUriFactory.getInstance().create(urlSupplier, service, method);
        final RestParameterAdaptor.Client methodArgs = RestParameterAdaptor.Client.Factory.create(method);
        final RestResponseAdaptor.Client<U> responseAdaptor = RestResponseAdaptor.Client.Factory.create(method);
        return new RestClientMethod<>(method, methodUrlSupplier, methodArgs, responseAdaptor);
    }
}
