package com.github.tcurrie.rest.factory.proxy;

import com.openpojo.business.BusinessIdentity;
import com.openpojo.business.annotation.BusinessKey;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class ProxyInvocationHandler<T extends ProxyMethod<?>> implements InvocationHandler {
    @BusinessKey private final Map<Method, T> methodHandlers;

    public static <T extends ProxyMethod<?>> ProxyInvocationHandler<T> create(final Stream<T> methodHandlers) {
        return new ProxyInvocationHandler<>(methodHandlers.collect(Collectors.toMap(ProxyMethod::getMethod, m->m)));
    }

    private ProxyInvocationHandler(final Map<Method, T> methodHandlers) {
        this.methodHandlers = methodHandlers;
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        if (ProxyMethodHandler.METHOD.equals(method)) {
            return methodHandlers.values().stream().collect(Collectors.toSet());
        } else if (Methods.EQUALS.equals(method)) {
            return equals(args[0]);
        } else if (Methods.HASH_CODE.equals(method)) {
            return hashCode();
        } else {
            return methodHandlers.get(method).invoke(args);
        }
    }

    @Override
    public int hashCode() {
        return BusinessIdentity.getHashCode(this);
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(final Object obj) {
        return BusinessIdentity.areEqual(this, obj);
    }
}
