package com.github.tcurrie.rest.factory.service;


import com.github.tcurrie.rest.factory.RestParameterAdaptor;
import com.github.tcurrie.rest.factory.RestResponseAdaptor;
import com.github.tcurrie.rest.factory.RestUriFactory;
import com.github.tcurrie.rest.factory.proxy.MethodImplementation;
import com.github.tcurrie.rest.factory.proxy.Methods;
import com.github.tcurrie.rest.factory.v1.RestFactoryException;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.stream.Stream;

final class RestMethodFactory {
    private RestMethodFactory() {
        throw new RestFactoryException("Can not construct instance of Factory class.");
    }

    static Stream<RestServiceMethod> create(final Collection<Object> beans) {
        return beans.stream().flatMap(RestMethodFactory::create);
    }

    static Stream<RestServiceMethod> create(final Object bean) {
        return Methods.BeanFactory.stream(bean)
                .filter(method -> !method.getDeclaringClass().getCanonicalName().contains("org.springframework.aop"))
                .map(method -> create(bean, method.getDeclaringClass(), method));
    }

    @SuppressWarnings("unchecked")
    private static <T, U> RestServiceMethod<T, U> create(final Object bean, final Class<T> type, final Method method) {
        final String uri = RestUriFactory.getInstance().create(type, method);
        final RestParameterAdaptor.Service requestAdaptor = RestParameterAdaptor.Service.Factory.create(method);
        final RestResponseAdaptor.Service<U> resultAdaptor = RestResponseAdaptor.Service.Factory.create();
        return new RestServiceMethod<>(uri, MethodImplementation.create((T) bean, method), requestAdaptor, resultAdaptor);
    }
}
