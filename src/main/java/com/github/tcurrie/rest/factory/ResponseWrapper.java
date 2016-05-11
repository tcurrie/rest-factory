package com.github.tcurrie.rest.factory;

public class ResponseWrapper<T> {
    private T result;

    public static <T> ResponseWrapper<T> create(final T result) {
        return new ResponseWrapper<>(result);
    }

    private ResponseWrapper() {}

    private ResponseWrapper(final T result) {
        this.result = result;
    }

    public T getResult() {
        return result;
    }
}
