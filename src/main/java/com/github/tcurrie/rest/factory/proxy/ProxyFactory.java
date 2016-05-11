package com.github.tcurrie.rest.factory.proxy;

import java.lang.reflect.Proxy;

public class ProxyFactory {
    @SuppressWarnings("unchecked")
    public static <T, U extends ProxyMethod<?>> T create(Class<T> type, final Methods.TypeFactory<U> factory) {
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type},
                ProxyInvocationHandler.create(Methods.TypeFactory.map(type, factory)));
    }
}
