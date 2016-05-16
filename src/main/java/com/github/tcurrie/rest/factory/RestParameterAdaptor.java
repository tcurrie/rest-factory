package com.github.tcurrie.rest.factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tcurrie.rest.factory.model.RestFactoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Method;
import java.util.function.Function;

public interface RestParameterAdaptor {
    interface Client extends Function<Object[], String> {
        final class Factory {
            private Factory() {
                throw RestFactoryException.create("Can not construct instance of Factory class.");
            }

            private static final Logger LOGGER = LoggerFactory.getLogger(Factory.class);
            private static final ObjectMapper MAPPER = new ObjectMapper();
            public static Client create(final Method method) {
                return p -> {
                    try {
                        return MAPPER.writeValueAsString(p);
                    } catch (final JsonProcessingException e) {
                        LOGGER.warn("Failed to map [{}] for method [{}].", p, method, e);
                        throw RestFactoryException.create(Strings.format("Failed to map [{}] for method [{}].", p, method), e);
                    }
                };
            }
        }
    }


    interface Service extends Function<HttpServletRequest, Object[]> {
        final class Factory {
            private Factory() {
                throw RestFactoryException.create("Can not construct instance of Factory class.");
            }

            private static final Logger LOGGER = LoggerFactory.getLogger(Factory.class);
            private static final Service NO_ARGUMENT_ADAPTOR = r -> new Object[0];

            public static Service create(final Method method) {
                if (method.getParameterCount() == 0) {
                    return NO_ARGUMENT_ADAPTOR;
                } else {
                    final Function<Reader, Object[]> parser = JsonAdaptor.Factory.create(method);
                    return r -> {
                        try {
                            return parser.apply(r.getReader());
                        } catch (final IOException e) {
                            LOGGER.warn("Failed to read arguments for method [{}].", method, e);
                            throw RestFactoryException.create(Strings.format("Failed to read arguments for method [{}].", method), e);
                        }
                    };
                }
            }
        }
    }

}
