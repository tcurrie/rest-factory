package com.github.tcurrie.rest.factory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tcurrie.rest.factory.model.RestFactoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Reader;
import java.util.Arrays;
import java.util.function.Function;

public interface JsonAdaptor extends Function<Reader, Object[]> {
    class Factory {
        private static final Logger LOGGER = LoggerFactory.getLogger(JsonAdaptor.class);
        private static final ObjectMapper MAPPER = new ObjectMapper();

        public static JsonAdaptor create(final Class<?>[] parameterTypes) {
            LOGGER.info("Creating Json adaptor from Reader to [{}]", Arrays.toString(parameterTypes));
            validateParameters(parameterTypes);

            return r -> {
                final Object[] args = new Object[parameterTypes.length];
                try {
                    final JsonParser p = new JsonFactory().createParser(r);
                    p.nextToken();
                    for (int i = 0; i < parameterTypes.length; i++) {
                        p.nextToken();
                        args[i] = MAPPER.readValue(p, parameterTypes[i]);
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
                    .filter(Factory::notPrimitive)
                    .filter(Factory::notPrimitiveArray)
                    .forEach(p -> {
                        try {
                            p.getDeclaredConstructor();
                        } catch (final NoSuchMethodException e) {
                            LOGGER.error("Can not wire adaptor for parameter [{}] type has no default constructor.", p, e);
                            throw RestFactoryException.create(Strings.format("Can not wire adaptor for parameter [{}] type has no default constructor.", p), e);
                        }
                    });
        }

        private static boolean notPrimitive(final Class<?> p) {
            return !p.isPrimitive();
        }

        private static boolean notPrimitiveArray(final Class<?> p) {
            return !p.isArray() || !p.getComponentType().isPrimitive();
        }
    }
}
