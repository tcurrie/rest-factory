package com.github.tcurrie.rest.factory;

import com.github.tcurrie.rest.factory.v1.ExceptionWrapper;
import com.github.tcurrie.rest.factory.v1.RestFactoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

interface RestExceptionAdaptor {
    interface Client {
        class Factory {
            private Factory() {
                throw new RestFactoryException("Can not construct instance of Factory class.");
            }

            private static final Logger LOGGER = LoggerFactory.getLogger(Factory.class);
            public static Throwable create(final ExceptionWrapper wrapper) {
                try {
                    LOGGER.debug("Adapting exception result [{}] from response.", wrapper.getMessage());
                    return (Throwable) wrapper.getExceptionType().getConstructor(String.class, Throwable.class).newInstance(wrapper.getMessage(),
                            wrapper.getExceptionType().getConstructor(String.class).newInstance(wrapper.getStackTrace())
                    );
                } catch (final Exception e) {
                    e.printStackTrace();
                    LOGGER.warn("Failed to adapt exception [{}] from response.", wrapper.getStackTrace(), e);
                    throw new RestFactoryException(Strings.format("Failed to adapt exception [{}] from response.", wrapper.getStackTrace()), e);
                }
            }
        }
    }

    final class Service implements Function<Throwable, ExceptionWrapper> {
        private static final Logger LOGGER = LoggerFactory.getLogger(Service.class);
        static final Service INSTANCE = new Service();
        private Service() {}
        public ExceptionWrapper apply(final Throwable throwable) {
            LOGGER.debug("Adapting exception result [{}] to response.", new Object[]{throwable});
            return ExceptionWrapper.createException(throwable);
        }
    }
}
