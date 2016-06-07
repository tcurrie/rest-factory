package com.github.tcurrie.rest.factory.proxy;

import com.github.tcurrie.rest.factory.v1.RestFactoryException;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.function.Function;

public final class ProxyFactory {
    private ProxyFactory() {
        throw new RestFactoryException("Can not construct instance of Factory class.");
    }

    @SuppressWarnings("unchecked")
    public static <T, U extends ProxyMethod<?>> T create(Class<T> type, final Function<Method, U> factory) {
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type, ProxyMethodHandler.class},
                ProxyInvocationHandler.create(Methods.TypeFactory.stream(type).map(factory)));
    }
}
