package com.github.tcurrie.rest.factory.proxy;

import com.github.tcurrie.rest.factory.Strings;
import com.github.tcurrie.rest.factory.v1.RestFactoryException;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Stream;

public interface Methods {
    Method EQUALS = TypeFactory.get(Object.class, "equals", Object.class);
    Method HASH_CODE = TypeFactory.get(Object.class, "hashCode");

    interface BeanFactory<U> extends Function<Class<?>, TypeFactory<U>> {
        static <U> Stream<U> map(final Object bean, final BeanFactory<U> factory) {
            return Arrays.stream(bean.getClass().getInterfaces()).flatMap(
                    type -> TypeFactory.map(type, factory.apply(type)));
        }
    }

    interface TypeFactory<U> extends Function<Method, U> {
        static <U> Stream<U> map(final Class<?> type, final TypeFactory<U> factory) {
            return Arrays.stream(type.getMethods()).map(factory);
        }

        static Method get(Class<?> type, String methodName, Class<?>... args) {
            try {
                return type.getMethod(methodName, args);
            } catch (NoSuchMethodException e) {
                throw new RestFactoryException(Strings.format("Unable to find class[{}], method[{}], args[{}].", type, methodName, args));
            }
        }
    }
}
