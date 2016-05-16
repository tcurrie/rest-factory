package com.github.tcurrie.rest.factory.model;

import com.github.tcurrie.rest.factory.Strings;

import java.io.Serializable;

public class ExceptionWrapper implements Serializable {
    private static final long serialVersionUID = 1L;
    private Class<?> exceptionType;
    private String message;
    private String stackTrace;

    public static ExceptionWrapper createException(final Throwable exception) {
        final String trace = Strings.getStackTrace(exception);
        return new ExceptionWrapper(exception.getClass(), exception.getMessage(), trace);
    }

    private ExceptionWrapper(final Class<?> exceptionType, final String message, final String stackTrace) {
        this();
        this.exceptionType = exceptionType;
        this.message = message;
        this.stackTrace = stackTrace;
    }

    private ExceptionWrapper() {}

    public String getStackTrace() {
        return stackTrace;
    }

    public Class<?> getExceptionType() {
        return exceptionType;
    }

    public String getMessage() {
        return message;
    }
}
