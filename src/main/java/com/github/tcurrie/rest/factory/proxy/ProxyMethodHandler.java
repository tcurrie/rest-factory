package com.github.tcurrie.rest.factory.proxy;

import java.lang.reflect.Method;
import java.util.Set;

public interface ProxyMethodHandler<T extends ProxyMethod<?>> {
    Set<T> getMethodHandlers();
    Method METHOD = ProxyMethodHandler.class.getMethods()[0];
}
