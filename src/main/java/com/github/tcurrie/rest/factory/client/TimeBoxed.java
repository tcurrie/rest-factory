package com.github.tcurrie.rest.factory.client;

import com.github.tcurrie.rest.factory.v1.RestFactoryException;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

final class TimeBoxed {
    private TimeBoxed() {
        throw new RestFactoryException("Can not construct instance of Factory class.");
    }

    static <T> T attempt(final Supplier<T> work, final int timeout, final TimeUnit timeUnit) {
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        final Future<T> future = executor.submit(work::get);
        executor.shutdown();
        try {
            return future.get(timeout, timeUnit);
        } catch (final Exception e) {
            throw wrapWithRuntimeException(unwrapExecutionException(e));
        } finally {
            executor.shutdownNow();
        }
    }

    private static RuntimeException wrapWithRuntimeException(final Throwable t) {
        if (RuntimeException.class.isAssignableFrom(t.getClass())) {
            return (RuntimeException) t;
        } else {
            return new RestFactoryException("Failed to complete task.", t);
        }
    }

    private static Throwable unwrapExecutionException(final Throwable e) {
        if (ExecutionException.class.isAssignableFrom(e.getClass())) {
            return e.getCause();
        } else {
            return e;
        }
    }
}
