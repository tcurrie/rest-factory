package com.github.tcurrie.rest.factory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

import com.openpojo.random.RandomFactory;
import com.openpojo.random.RandomGenerator;

public final class MethodRandomGenerator implements RandomGenerator {
    private static final MethodRandomGenerator instance = new MethodRandomGenerator();
    static {
        RandomFactory.addRandomGenerator(instance);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<Class<?>> getTypes() {
        return Arrays.asList(new Class[]{Method.class});
    }

    @Override
    public Object doGenerate(final Class<?> type) {
        return getClass().getMethods()[new Random().nextInt(getClass().getMethods().length)];
    }

    @SuppressWarnings("EmptyMethod")
    public static synchronized void create() {
    }
}
