package com.github.tcurrie.rest.factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Method;
import java.util.function.Function;
import java.util.logging.Logger;

public interface RestParameterAdaptor {
    interface Client extends Function<Object[], String> {
        final class Factory {
            private static final Logger LOGGER = Logger.getLogger(RestParameterAdaptor.class.getName());
            private static final ObjectMapper MAPPER = new ObjectMapper();
            public static Client create(final Method method) {
                return p -> {
                    try {
                        return MAPPER.writeValueAsString(p);
                    } catch (final JsonProcessingException e) {
                        throw RestFactoryException.create(LOGGER, "Failed to map [{0}].", e, p);
                    }
                };
            }
        }
    }


    interface Service extends Function<HttpServletRequest, Object[]> {
        final class Factory {
            private static final Logger LOGGER = Logger.getLogger(RestParameterAdaptor.class.getName());
            private static final Service NO_ARGUMENT_ADAPTOR = r -> new Object[0];

            public static Service create(final Method method) {
                if (method.getParameterCount() == 0) {
                    return NO_ARGUMENT_ADAPTOR;
                } else {
                    final Function<Reader, Object[]> parser = JsonAdaptor.Factory.create(method.getParameterTypes());
                    return r -> {
                        try {
                            return parser.apply(r.getReader());
                        } catch (final IOException e) {
                            throw RestFactoryException.create(LOGGER, "Failed to read arguments.", e);
                        }
                    };
                }
            }

        }
    }

}
