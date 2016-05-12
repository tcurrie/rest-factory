package com.github.tcurrie.rest.factory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Reader;
import java.util.Arrays;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public interface JsonAdaptor extends Function<Reader, Object[]> {
    class Factory {
        private static final Logger LOGGER = Logger.getLogger(RestParameterAdaptor.class.getName());
        private static final ObjectMapper MAPPER = new ObjectMapper();

        public static JsonAdaptor create(final Class<?>[] parameterTypes) {
            LOGGER.log(Level.INFO, "Creating Json adaptor from Reader to [{0}]", Arrays.toString(parameterTypes));
            validateParameters(parameterTypes);

            return r -> {
                final Object[] args = new Object[parameterTypes.length];
                try {
                    final JsonParser p = new JsonFactory().createParser(r);
                    p.nextToken();
                    for (int i = 0; i < parameterTypes.length; i++) {
                        p.nextToken();
                        args[i] = MAPPER.readValue(p, parameterTypes[i]);
                        LOGGER.log(Level.FINEST, "Parsed arg [{0}], type [{1}] as [{2}].", new Object[] {i, parameterTypes[i], args[i]});
                    }
                    return args;
                } catch (final Exception e) {
                    throw RestFactoryException.create(LOGGER, "Failed to read arguments, got [{0}].", e, Arrays.toString(args));
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
                            throw RestFactoryException.create(LOGGER, "Can not wire adaptor for parameter [{0}] type has no default constructor.", e, p);
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
