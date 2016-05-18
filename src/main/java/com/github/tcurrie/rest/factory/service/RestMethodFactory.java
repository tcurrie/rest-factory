package com.github.tcurrie.rest.factory.service;


import com.github.tcurrie.rest.factory.v1.RestFactoryException;
import com.github.tcurrie.rest.factory.proxy.Methods;
import com.github.tcurrie.rest.factory.RestParameterAdaptor;
import com.github.tcurrie.rest.factory.RestResponseAdaptor;
import com.github.tcurrie.rest.factory.RestUriFactory;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.stream.Stream;

final class RestMethodFactory {
    private RestMethodFactory() {
        throw RestFactoryException.create("Can not construct instance of Factory class.");
    }

    static Stream<RestMethod> create(final Collection<Object> beans) {
        return beans.stream().flatMap(RestMethodFactory::create);
    }

    static Stream<RestMethod> create(final Object bean) {
        return Methods.BeanFactory.map(bean, type -> (method -> create(bean, type, method)));
    }

    @SuppressWarnings("unchecked")
    private static <T, U> RestMethod<T, U> create(final Object bean, final Class<T> type, final Method method) {
        final String uri = RestUriFactory.getInstance().create(type, method);
        final RestParameterAdaptor.Service requestAdaptor = RestParameterAdaptor.Service.Factory.create(method);
        final RestResponseAdaptor.Service<U> resultAdaptor = RestResponseAdaptor.Service.Factory.create();
        return new RestMethod<>(uri, method, (T) bean, requestAdaptor, resultAdaptor);
    }
}
