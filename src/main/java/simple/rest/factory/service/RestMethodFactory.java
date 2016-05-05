package simple.rest.factory.service;


import simple.rest.factory.RestParameterAdaptor;
import simple.rest.factory.RestResponseAdaptor;
import simple.rest.factory.RestUriFactory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

final class RestMethodFactory {
    static Set<RestMethod> create(final Map<String, Object> beans) {
        return beans.values().stream().flatMap(bean -> create(bean).stream()).collect(Collectors.toSet());
    }

    static Set<RestMethod> create(final Object bean) {
        return Arrays.stream(bean.getClass().getInterfaces()).flatMap(
                type -> Arrays.stream(type.getMethods()).map(
                        method -> create(bean, type, method)))
                .collect(Collectors.toSet());
    }


    @SuppressWarnings("unchecked")
    private static <T, U> RestMethod<T, U> create(final Object bean, final Class<T> type, final Method method) {
        final String uri = RestUriFactory.getInstance().create(type, method);
        final RestParameterAdaptor.Service requestAdaptor = RestParameterAdaptor.Service.Factory.create(method);
        final RestResponseAdaptor<U> resultAdaptor = RestResponseAdaptor.Factory.create();
        return new RestMethod<T, U>(uri, method, (T) bean, requestAdaptor, resultAdaptor);
    }
}
