package com.github.tcurrie.rest.factory.model;

public final class RestFactoryException extends RuntimeException {
    public static RestFactoryException create(final String message) {
        return new RestFactoryException(message);
    }
    public static RestFactoryException create(final String message, final Throwable cause) {
        return new RestFactoryException(message, cause);
    }

    private RestFactoryException(final String message) {
        super(message);
    }

    private RestFactoryException(final String message, final Throwable cause) {
        super(message, cause);
    }


}
