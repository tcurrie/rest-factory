package com.github.tcurrie.rest.factory;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public interface RestExceptionAdaptor {
    interface Client {
        class Factory {
            private static final Logger LOGGER = Logger.getLogger(Client.class.getName());
            public static <T> Throwable create(final ResponseWrapper<T> wrapper) {
                try {
                    return (Throwable) wrapper.getExceptionType().getConstructor(String.class).newInstance(wrapper.getException());
                } catch (final Exception e) {
                    throw RestFactoryException.create(LOGGER, Level.WARNING, "Failed to adapt result [{0}] to response.", e, wrapper.getException());
                }
            }
        }
    }
    interface Service {
        class Factory {
            private static final ObjectMapper MAPPER = new ObjectMapper();
            private static final Logger LOGGER = Logger.getLogger(Service.class.getName());
            public static <T> Consumer<HttpServletResponse> apply(final Throwable exception) {
                return response -> {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setHeader("Content-Type", "application/json");
                    try {
                        LOGGER.log(Level.FINE, "Adapting exception result [{0}] to response.", new Object[]{exception});
                        MAPPER.writeValue(response.getWriter(), ResponseWrapper.createException(exception));
                    } catch (final IOException e) {
                        throw RestFactoryException.create(LOGGER, Level.WARNING, "Failed to adapt result [{0}] to response.", e, exception);
                    }
                };
            }
        }
    }
}
