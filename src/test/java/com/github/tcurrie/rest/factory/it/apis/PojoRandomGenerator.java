package com.github.tcurrie.rest.factory.it.apis;

import com.google.common.collect.Lists;
import com.openpojo.random.RandomFactory;
import com.openpojo.random.RandomGenerator;

import java.util.Collection;

import static com.openpojo.random.RandomFactory.getRandomValue;

public final class PojoRandomGenerator implements RandomGenerator {
    private static final PojoRandomGenerator instance = new PojoRandomGenerator();
    static {
        RandomFactory.addRandomGenerator(instance);
    }

    @Override
    public Collection<Class<?>> getTypes() {
        return Lists.newArrayList(Pojo.class);
    }

    @Override
    public Object doGenerate(final Class<?> type) {
        return new Pojo(getRandomValue(String.class), getRandomValue(int[].class));
    }

    @SuppressWarnings("EmptyMethod")
    public static synchronized void create() {
    }
}
