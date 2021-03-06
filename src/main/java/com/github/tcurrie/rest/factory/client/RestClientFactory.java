package com.github.tcurrie.rest.factory.client;

import com.github.tcurrie.rest.factory.EchoResponseAdaptor;
import com.github.tcurrie.rest.factory.RestParameterAdaptor;
import com.github.tcurrie.rest.factory.RestResponseAdaptor;
import com.github.tcurrie.rest.factory.RestUri;
import com.github.tcurrie.rest.factory.RestUriFactory;
import com.github.tcurrie.rest.factory.proxy.ProxyFactory;
import com.github.tcurrie.rest.factory.v1.RestClientMonitor;
import com.github.tcurrie.rest.factory.v1.RestFactoryException;
import com.github.tcurrie.rest.factory.v1.RestMethodVerificationResult;
import com.github.tcurrie.rest.factory.v1.TimeOut;
import com.openpojo.random.RandomFactory;
import com.openpojo.reflection.PojoMethod;
import com.openpojo.reflection.impl.PojoMethodFactory;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class RestClientFactory {
    private RestClientFactory() {
        throw new RestFactoryException("Can not construct instance of Factory class.");
    }

    public static <T> T create(final Class<T> service, final Supplier<String> urlSupplier, final Supplier<TimeOut> timeOutSupplier) {
        final T proxy = ProxyFactory.create(service, RestClient.class, method -> create(service, urlSupplier, timeOutSupplier, method));
        RestClientMonitorImpl.addHandler(proxy);
        return proxy;
    }

    private static <T, U> RestClientMethod<U> create(final Class<T> service, final Supplier<String> urlSupplier, final Supplier<TimeOut> timeOutSupplier, final Method method) {
        final RestUri methodUrlSupplier = RestUriFactory.getInstance().create(urlSupplier, service, method);
        final RestParameterAdaptor.Client methodArgs = RestParameterAdaptor.Client.Factory.create(method);
        final RestResponseAdaptor.Client<U> responseAdaptor = RestResponseAdaptor.Client.Factory.create(method);
        final EchoResponseAdaptor.Client echoAdaptor = EchoResponseAdaptor.Client.Factory.create(method);
        return new RestClientMethod<>(method, methodUrlSupplier, timeOutSupplier, methodArgs, responseAdaptor, echoAdaptor);
    }

    public static <T> Set<RestMethodVerificationResult> verify(final T client) {
        return ((RestClient)client).getMethodHandlers().stream().map(m -> m.echo(createRandomArguments(m.getMethod()))).collect(Collectors.toSet());
    }

    private static Object[] createRandomArguments(final Method m) {
        @SuppressWarnings("ConfusingArgumentToVarargsMethod")
        final PojoMethod pm = PojoMethodFactory.getMethod(m.getDeclaringClass(), m.getName(), m.getParameterTypes());
        return pm.getPojoParameters().stream().map(RandomFactory::getRandomValue).toArray();
    }

    @SuppressWarnings("WeakerAccess")
    public static <T> Supplier<String> getUrlSupplier(final T client) {
        //noinspection OptionalGetWithoutIsPresent
        return ((RestClient)client).getMethodHandlers().stream().map(m -> m.getMethodUrlSupplier().getUrlSupplier()).findFirst().get();
    }

    @SuppressWarnings("WeakerAccess")
    public static <T> Supplier<TimeOut> getTimeoutSupplier(final T client) {
        //noinspection OptionalGetWithoutIsPresent
        return ((RestClient)client).getMethodHandlers().stream().map(RestClientMethod::getTimeOutSupplier).findFirst().get();
    }

    public static Set<RestMethodVerificationResult> verify(final String url, final TimeOut timeOut) {
        return create(RestClientMonitor.class, ()->url, ()->timeOut).verifyClients();
    }
}

