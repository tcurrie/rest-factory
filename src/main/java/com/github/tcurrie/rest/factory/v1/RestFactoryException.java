package com.github.tcurrie.rest.factory.v1;

public final class RestFactoryException extends RuntimeException {
    public RestFactoryException(final String message) {
        super(message);
    }

    public RestFactoryException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
