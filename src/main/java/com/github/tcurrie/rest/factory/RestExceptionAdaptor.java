package com.github.tcurrie.rest.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tcurrie.rest.factory.v1.ExceptionWrapper;
import com.github.tcurrie.rest.factory.v1.ResponseWrapper;
import com.github.tcurrie.rest.factory.v1.RestFactoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.function.Consumer;

public interface RestExceptionAdaptor {
    interface Client {
        class Factory {
            private Factory() {
                throw RestFactoryException.create("Can not construct instance of Factory class.");
            }

            private static final Logger LOGGER = LoggerFactory.getLogger(Factory.class);
            public static Throwable create(final ExceptionWrapper wrapper) {
                try {
                    LOGGER.debug("Adapting exception result [{}] from response.", wrapper.getMessage());
                    return (Throwable) wrapper.getExceptionType().getConstructor(String.class, Throwable.class).newInstance(wrapper.getMessage(),
                            wrapper.getExceptionType().getConstructor(String.class).newInstance(wrapper.getStackTrace())
                    );
                } catch (final Exception e) {
                    LOGGER.warn("Failed to adapt exception [{}] from response.", wrapper.getStackTrace(), e);
                    throw RestFactoryException.create(Strings.format("Failed to adapt exception [{}] from response.", wrapper.getStackTrace()), e);
                }
            }
        }
    }
    interface Service {
        class Factory {
            private Factory() {
                throw RestFactoryException.create("Can not construct instance of Factory class.");
            }

            private static final ObjectMapper MAPPER = new ObjectMapper();
            private static final Logger LOGGER = LoggerFactory.getLogger(Factory.class);
            public static Consumer<HttpServletResponse> apply(final Throwable throwable) {
                return response -> {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setHeader("Content-Type", "application/json");
                    try {
                        LOGGER.debug("Adapting exception result [{}] to response.", new Object[]{throwable});
                        MAPPER.writeValue(response.getWriter(), ResponseWrapper.createException(ExceptionWrapper.createException(throwable)));
                    } catch (final IOException e) {
                        LOGGER.warn("Failed to adapt exception [{}] to response.", throwable.getStackTrace(), e);
                        throw RestFactoryException.create(Strings.format("Failed to adapt exception [{}] to response.", Strings.getStackTrace(throwable)), e);
                    }
                };
            }
        }
    }
}
