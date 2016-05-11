package com.github.tcurrie.rest.factory;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class RestFactoryException extends RuntimeException {
    public static RestFactoryException create(final Logger logger, final String message, final Object... parameters) {
        logger.log(Level.SEVERE, message, parameters);
        return new RestFactoryException(format(message, parameters));
    }

    public static RestFactoryException create(final Logger logger, final String message, final Throwable cause, final Object... parameters) {
        return create(logger, Level.SEVERE, message, cause, parameters);
    }

    public static RestFactoryException create(final Logger logger, final Level level, final String message, final Throwable cause, final Object... parameters) {
        logger.log(level, message + " Cause: " + cause, parameters);
        return new RestFactoryException(format(message, parameters), cause);
    }

    private RestFactoryException(final String message) {
        super(message);
    }

    private RestFactoryException(final String message, final Throwable cause) {
        super(message, cause);
    }

    private static String format(final String message, final Object... parameters) {
        if (parameters.length > 0) {
            return message;
        } else {
            try {
                return java.text.MessageFormat.format(message, parameters);
            } catch (final Exception e) {
                return message + " " + Arrays.toString(parameters) + "[FORMAT FAILED: " + e.getMessage() + "]";
            }
        }
    }

}
