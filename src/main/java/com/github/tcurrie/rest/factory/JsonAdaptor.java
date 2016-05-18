package com.github.tcurrie.rest.factory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tcurrie.rest.factory.v1.RestFactoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface JsonAdaptor extends Function<String, Object[]> {
    class Factory {
        private Factory() {
            throw RestFactoryException.create("Can not construct instance of Factory class.");
        }

        private static final Logger LOGGER = LoggerFactory.getLogger(Factory.class);
        private static final ObjectMapper MAPPER = new ObjectMapper();
        private static final Predicate<Class<?>> IS_NOT_INTERFACE = p -> !p.isInterface();
        private static final Predicate<Class<?>> IS_NOT_ARRAY = (t1) -> !t1.isArray();
        private static final Predicate<Class<?>> IS_NOT_PRIMITIVE = (t) -> !t.isPrimitive();

        public static JsonAdaptor create(final Method method) {
            final Class<?>[] parameterTypes = method.getParameterTypes();
            LOGGER.info("Creating Json adaptor from Reader to [{}]", Arrays.toString(parameterTypes));
            validateParameters(parameterTypes);
            final List<JavaType> javaTypes =
                    Arrays.stream(method.getGenericParameterTypes()).map(t->
                    MAPPER.getTypeFactory().constructType(t)).collect(Collectors.toList());
            return r -> {
                final Object[] args = new Object[parameterTypes.length];
                try {
                    final JsonParser p = new JsonFactory().createParser(r);
                    p.nextToken();
                    for (int i = 0; i < parameterTypes.length; i++) {
                        p.nextToken();
                        args[i] = MAPPER.readValue(p, javaTypes.get(i));
                        LOGGER.debug("Parsed arg [{}], type [{}] as [{}].", i, parameterTypes[i], args[i]);
                    }
                    return args;
                } catch (final Exception e) {
                    LOGGER.warn("Failed to read arguments, got [{}].", Arrays.toString(args), e);
                    throw RestFactoryException.create(Strings.format("Failed to read arguments, got [{}].", Arrays.toString(args)), e);
                }
            };
        }

        private static void validateParameters(final Class<?>[] parameterTypes) {
            Arrays.stream(parameterTypes)
                    .filter(IS_NOT_PRIMITIVE)
                    .filter(IS_NOT_ARRAY)
                    .filter(IS_NOT_INTERFACE)
                    .forEach(p -> {
                        try {
                            p.getDeclaredConstructor();
                        } catch (final NoSuchMethodException e) {
                            LOGGER.error("Can not wire adaptor for parameter [{}] type has no default constructor.", p, e);
                            throw RestFactoryException.create(Strings.format("Can not wire adaptor for parameter [{}] type has no default constructor.", p), e);
                        }
                    });
        }

    }
}
