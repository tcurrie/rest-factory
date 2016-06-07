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

    interface BeanFactory {
        static Stream<Method> stream(final Object bean) {
            return Arrays.stream(bean.getClass().getInterfaces()).flatMap(TypeFactory::stream);
        }

        static <T> Stream<T> map(final Object bean, Function<Class<?>, Function<Method, T>> function) {
            return stream(bean).map(m -> function.apply(m.getDeclaringClass()).apply(m));
        }
    }

    interface TypeFactory {
        static Stream<Method> stream(final Class<?> type) {
            return Arrays.stream(type.getMethods());
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
