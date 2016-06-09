package com.github.tcurrie.rest.factory.proxy;

import com.openpojo.business.BusinessIdentity;
import com.openpojo.business.annotation.BusinessKey;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class MethodImplementation<T, U> implements Comparable<MethodImplementation<?,?>> {
    @BusinessKey private final Class<?> type;
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    @BusinessKey private final String name;
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    @BusinessKey private final int parameters;
    private final Method method;
    private final T bean;

    public static <T, U> MethodImplementation<T,U> create(final T bean, final Method method) {
        return new MethodImplementation<>(bean, method);
    }

    private MethodImplementation(final T bean, final Method method) {
        this.type = method.getDeclaringClass();
        this.name = method.getName();
        this.parameters = method.getParameterCount();
        this.bean = bean;
        this.method = method;
    }

    public U invoke(final Object[] args) throws Throwable {
        try {
            //noinspection unchecked
            return (U) method.invoke(bean, args);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

    public String getApi() {
        return method.toGenericString();
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(final Object obj) {
        return BusinessIdentity.areEqual(this, obj);
    }

    @Override
    public int hashCode() {
        return BusinessIdentity.getHashCode(this);
    }

    @Override
    public String toString() {
        return BusinessIdentity.toString(this);
    }

    @Override
    public int compareTo(final MethodImplementation<?, ?> o) {
        final Class<?>[] parameterTypesA = method.getParameterTypes();
        final Class<?>[] parameterTypesB = o.method.getParameterTypes();
        int result = type.getName().compareTo(o.type.getName());

        if (result == 0) {
            result = method.getName().compareTo(o.method.getName());
            if (result == 0) {
                result = Integer.compare(parameterTypesB.length, parameterTypesA.length);
            }
        }
        return result;

    }
}
