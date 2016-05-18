package com.github.tcurrie.rest.factory;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.github.tcurrie.rest.factory.v1.ResponseWrapper;
import com.github.tcurrie.rest.factory.v1.RestFactoryException;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.function.Consumer;
import java.util.function.Function;

public interface RestResponseAdaptor {
    interface Client<T> {
        T apply(String s) throws Throwable;

        final class Factory {
            private Factory() {
                throw RestFactoryException.create("Can not construct instance of Factory class.");
            }

            private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Factory.class);
            private static final ObjectMapper MAPPER = new ObjectMapper();
            private static final TypeFactory TYPE_FACTORY = MAPPER.getTypeFactory();

            public static <T> Client<T> create(final Method method) {
                @SuppressWarnings("unchecked")
                final Class<T> methodResult = (Class<T>) method.getReturnType();
                final JavaType type;
                if (void.class.equals(methodResult)) {
                    type = TYPE_FACTORY.constructParametrizedType(ResponseWrapper.class, ResponseWrapper.class, Object.class);
                } else {
                    type = TYPE_FACTORY.constructParametrizedType(ResponseWrapper.class, ResponseWrapper.class,
                            TYPE_FACTORY.constructType(method.getGenericReturnType()));
                }

                return result -> {
                    final ResponseWrapper<T> wrapper;
                    LOGGER.info("Adapting result from wrapper [{}]", result);
                    try {
                        wrapper = MAPPER.readValue(result, type);
                    } catch (final IOException e) {
                        LOGGER.warn("Failed to adapt result [{}] from response.", result, e);
                        throw RestFactoryException.create(Strings.format("Failed to adapt result [{}] from response.", result), e);
                    }
                    if (wrapper.isSuccess()) {
                        return wrapper.getResult();
                    } else {
                        throw RestExceptionAdaptor.Client.Factory.create(wrapper.getException());
                    }
                };
            }
        }
    }

    interface Service<T>  extends Function<T, Consumer<HttpServletResponse>> {
        final class Factory {
            private Factory() {
                throw RestFactoryException.create("Can not construct instance of Factory class.");
            }

            private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Factory.class);
            private static final ObjectMapper MAPPER = new ObjectMapper();

            public static <T> Service<T> create() {
                return result -> (response -> {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setHeader("Content-Type", "application/json");
                    try {
                        LOGGER.debug("Adapting result [{}] to response.", result);
                        MAPPER.writeValue(response.getWriter(), ResponseWrapper.create(result));
                    } catch (final IOException e) {
                        LOGGER.warn("Failed to adapt result [{}] to response.", result, e);
                        throw RestFactoryException.create(Strings.format("Failed to adapt result [{}] to response.", result), e);
                    }
                });
            }
        }
    }
}
