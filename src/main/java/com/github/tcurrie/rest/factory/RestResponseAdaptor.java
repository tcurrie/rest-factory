package com.github.tcurrie.rest.factory;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public interface RestResponseAdaptor {
    interface Service<T>  extends Function<T, Consumer<HttpServletResponse>> {
        final class Factory {
            private static final Logger LOGGER = Logger.getLogger(RestResponseAdaptor.class.getName());
            private static final ObjectMapper MAPPER = new ObjectMapper();

            public static <T> Service<T> create() {
                return result -> (response -> {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setHeader("Content-Type", "application/json");
                    try {
                        LOGGER.log(Level.FINE, "Adapting result [{0}] to response.", new Object[]{result});
                        MAPPER.writeValue(response.getWriter(), ResponseWrapper.create(result));
                    } catch (final IOException e) {
                        throw RestFactoryException.create(LOGGER, "Failed to adapt result [{0}] to response.", e, result);
                    }
                });
            }
        }
    }

    interface Client<T> {
        T apply(String s) throws Throwable;

        final class Factory {
            private static final Logger LOGGER = Logger.getLogger(RestResponseAdaptor.class.getName());
            public static <T> Client<T> create(final Method method) {
                @SuppressWarnings("unchecked")
                final Class<T> methodResult = (Class<T>) method.getReturnType();
                final JavaType type = new ObjectMapper().getTypeFactory().constructParametrizedType(ResponseWrapper.class, ResponseWrapper.class,
                        void.class.equals(methodResult) ? Object.class : methodResult);

                return result -> {
                    final ResponseWrapper<T> wrapper;
                    LOGGER.log(Level.FINE, "Adapting result wrapper [{0}]", result);
                    try {
                        wrapper = new ObjectMapper().readValue(result, type);
                    } catch (final IOException e) {
                        throw RestFactoryException.create(LOGGER, "Failed to adapt result [{0}] for method [{1}]", e, result, method);
                    }
                    if (wrapper.isSuccess()) {
                        return wrapper.getResult();
                    } else {
                        throw RestExceptionAdaptor.Client.Factory.create(wrapper);
                    }
                };
            }
        }
    }
}
