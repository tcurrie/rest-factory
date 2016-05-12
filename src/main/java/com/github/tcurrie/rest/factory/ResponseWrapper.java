package com.github.tcurrie.rest.factory;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ResponseWrapper<T> {
    private T result;
    private Class<?> exceptionType;
    private String exception;
    private boolean success;

    public static <T> ResponseWrapper<T> create(final T result) {
        return new ResponseWrapper<>(result);
    }

    public static ResponseWrapper<?> createException(final Throwable exception) {
        final ResponseWrapper w = new ResponseWrapper<>();
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        w.exception = exception.getMessage() + "\nCaused by: " + sw.toString();
        w.exceptionType = exception.getClass();
        w.success = false;
        return w;
    }

    private ResponseWrapper() {}

    private ResponseWrapper(final T result) {
        this.result = result;
        this.success = true;
    }

    public T getResult() {
        return result;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getException() {
        return exception;
    }

    public Class<?> getExceptionType() {
        return exceptionType;
    }
}
