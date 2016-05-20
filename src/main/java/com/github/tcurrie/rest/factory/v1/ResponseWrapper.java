package com.github.tcurrie.rest.factory.v1;

public class ResponseWrapper<T> {
    private T result;
    private ExceptionWrapper exception;
    private boolean success;

    public static <T> ResponseWrapper<T> create(final T result) {
        return new ResponseWrapper<>(result, true);
    }


    public static ResponseWrapper<Void> createException(final ExceptionWrapper exception) {
        return new ResponseWrapper<>(exception, false);
    }

    private ResponseWrapper() {}

    public ResponseWrapper(final ExceptionWrapper exception, final boolean success) {
        this.exception = exception;
        this.success = success;
    }

    private ResponseWrapper(final T result, final boolean success) {
        this.result = result;
        this.success = success;
    }

    public T getResult() {
        return result;
    }

    public boolean isSuccess() {
        return success;
    }

    public ExceptionWrapper getException() {
        return exception;
    }
}
