package com.github.tcurrie.rest.factory.proxy;

import java.lang.reflect.Method;

public interface ProxyMethod<T> {
    T invoke(Object[] args);
    Method getMethod();
}
