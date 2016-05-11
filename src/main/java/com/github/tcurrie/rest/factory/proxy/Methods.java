package com.github.tcurrie.rest.factory.proxy;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Stream;

public class Methods {
    public interface BeanFactory<U> extends Function<Class<?>, TypeFactory<U>> {
        static <U> Stream<U> map(final Object bean, final BeanFactory<U> factory) {
            return Arrays.stream(bean.getClass().getInterfaces()).flatMap(
                    type -> TypeFactory.map(type, factory.apply(type)));
        }
    }

    public interface TypeFactory<U> extends Function<Method, U> {
        static <U> Stream<U> map(final Class<?> type, final TypeFactory<U> factory) {
            return Arrays.stream(type.getMethods()).map(factory);
        }
    }
}
