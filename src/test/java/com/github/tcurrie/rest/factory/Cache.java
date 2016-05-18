package com.github.tcurrie.rest.factory;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class Cache<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Cache.class);
    private static final int TIMEOUT = 30;

    private final List<T> created;
    private final BlockingQueue<T> available;
    private final ExecutorService creators;
    private final AtomicBoolean run;
    private final Consumer<T> cleaner;

    public Cache(final int size, final Supplier<T> factory, final Consumer<T> cleaner) {
        this.created = Collections.synchronizedList(new ArrayList<>());
        this.available = new ArrayBlockingQueue<>(1);
        this.creators = Executors.newFixedThreadPool(size);
        this.run = new AtomicBoolean(true);
        this.cleaner = cleaner;

        initializeCreationThreads(size, factory);

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    private void initializeCreationThreads(final int size, final Supplier<T> factory) {
        final Supplier<T> createAndAdd = () -> {
            T item = factory.get();
            created.add(item);
            return item;
        };

        IntStream.range(0, size).forEach(i->creators.submit(()->{
            T item = createAndAdd.get();
            while (run.get()) {
                try {
                    if (available.offer(item, 1, TimeUnit.SECONDS) && run.get()) {
                        item = createAndAdd.get();
                    }
                } catch(Exception e) {
                    LOGGER.debug("Failed offer.", e);
                }
            }
        }));
    }

    private void shutdown() {
        this.run.set(false);
        this.creators.shutdown();
        try {
            this.creators.awaitTermination(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOGGER.debug("Failed clean termination.", e);
        }
        this.creators.shutdownNow();
        created.forEach(cleaner::accept);
    }

    public T borrow() {
        try {
            final T item = available.poll(TIMEOUT, TimeUnit.SECONDS);
            Assert.assertNotNull("Failed to borrow item in [" + TIMEOUT + "] seconds.", item);
            return item;
        } catch (InterruptedException e) {
            throw new RuntimeException("Failed to borrow.");
        }
    }

    public void remit(final T item) {
        try {
            available.offer(item);
        } catch (final Exception e) {
            LOGGER.debug("Failed to remit item.", e);
        }
    }

}
