package com.github.tcurrie.rest.factory.v1;

import com.github.tcurrie.rest.factory.Strings;

public class ExceptionWrapper {
    private Class<?> exceptionType;
    private String message;
    private String stackTrace;

    public static ExceptionWrapper createException(final Throwable exception) {
        final String trace = Strings.getStackTrace(exception);
        return new ExceptionWrapper(exception.getClass(), exception.getMessage(), trace);
    }

    private ExceptionWrapper(final Class<?> exceptionType, final String message, final String stackTrace) {
        this.exceptionType = exceptionType;
        this.message = message;
        this.stackTrace = stackTrace;
    }

    @SuppressWarnings("unused")
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
