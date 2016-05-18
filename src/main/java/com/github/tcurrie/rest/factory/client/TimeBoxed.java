package com.github.tcurrie.rest.factory.client;

import com.github.tcurrie.rest.factory.v1.RestFactoryException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

final class TimeBoxed {
    private TimeBoxed() {
        throw RestFactoryException.create("Can not construct instance of Factory class.");
    }

    static <T> T attempt(final Supplier<T> work, final int timeout, final TimeUnit timeUnit) {
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        final Future<T> future = executor.submit(work::get);
        executor.shutdown();
        try {
            return future.get(timeout, timeUnit);
        } catch (final Exception e) {
            throw RestFactoryException.create("", e);
        } finally {
            executor.shutdownNow();
        }
    }
}
