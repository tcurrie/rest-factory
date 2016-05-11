package com.github.tcurrie.rest.factory.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProxyInvocationHandler implements InvocationHandler {
    private final Map<Method, ProxyMethod<?>> methodHandlers;

    public static <T extends ProxyMethod<?>> ProxyInvocationHandler create(final Stream<T> methodHandlers) {
        return new ProxyInvocationHandler(methodHandlers.collect(Collectors.toMap(ProxyMethod::getMethod, m->m)));
    }

    private ProxyInvocationHandler(final Map<Method, ProxyMethod<?>> methodHandlers) {
        this.methodHandlers = methodHandlers;
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        return methodHandlers.get(method).invoke(args);
    }
}
