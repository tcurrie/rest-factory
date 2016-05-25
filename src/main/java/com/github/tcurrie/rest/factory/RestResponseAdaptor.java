package com.github.tcurrie.rest.factory;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.github.tcurrie.rest.factory.v1.ResponseWrapper;
import com.github.tcurrie.rest.factory.v1.RestFactoryException;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.function.Function;

public interface RestResponseAdaptor {
    interface Client<T> {
        T apply(String s) throws Throwable;

        final class Factory {
            private Factory() {
                throw new RestFactoryException("Can not construct instance of Factory class.");
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
                    LOGGER.debug("Adapting result from wrapper [{}]", result);
                    try {
                        wrapper = MAPPER.readValue(result, type);
                    } catch (final IOException e) {
                        LOGGER.warn("Failed to adapt result [{}] from response.", result, e);
                        throw new RestFactoryException(Strings.format("Failed to adapt result [{}] from response.", result), e);
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

    interface Service<T>  extends Function<T, String> {
        Service<Throwable> THROWABLE = Factory.createResponseFactory(t -> ResponseWrapper.createException(
                RestExceptionAdaptor.Service.INSTANCE.apply(t)));

        final class Factory {
            private Factory() {
                throw new RestFactoryException("Can not construct instance of Factory class.");
            }

            private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Factory.class);
            private static final ObjectMapper MAPPER = new ObjectMapper();

            public static <T> Service<T> create() {
                return createResponseFactory(ResponseWrapper::create);
            }

            private static <T, U> Service<T> createResponseFactory(final Function<T, ResponseWrapper<U>> f) {
                return result -> {
                    try {
                        LOGGER.debug("Adapting result [{}] to response.", result);
                        return MAPPER.writeValueAsString(f.apply(result));
                    } catch (final Exception e) {
                        LOGGER.warn("Failed to adapt result [{}] to response.", result, e);
                        throw new RestFactoryException(Strings.format("Failed to adapt result [{}] to response.", result), e);
                    }
                };
            }
        }
    }
}
