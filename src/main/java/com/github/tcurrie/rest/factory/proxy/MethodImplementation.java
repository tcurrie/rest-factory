package com.github.tcurrie.rest.factory.proxy;

import com.openpojo.business.BusinessIdentity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class MethodImplementation<T, U> {
    private final T bean;
    private final Method method;

    public static <T, U> MethodImplementation<T,U> create(final T bean, final Method method) {
        return new MethodImplementation<>(bean, method);
    }

    private MethodImplementation(final T bean, final Method method) {
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

    public String getMethodName() {
        return method.getName();
    }

    public String getBeanName() {
        return bean.getClass().getCanonicalName();
    }

    @Override
    public String toString() {
        return BusinessIdentity.toString(this);
    }

}
