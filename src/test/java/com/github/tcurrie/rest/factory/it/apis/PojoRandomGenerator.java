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
        return Lists.newArrayList(Pojo.class, SuperPojo.class, SubPojo.class);
    }

    @Override
    public Object doGenerate(final Class<?> type) {
        if (type.equals(Pojo.class)) {
            return new Pojo(getRandomValue(String.class), getRandomValue(int[].class));
        } else if (type.equals(SuperPojo.class)) {
            return new SuperPojo(getRandomValue(String.class), getRandomValue(int[].class));
        } else {
            return new SubPojo(getRandomValue(String.class), getRandomValue(int[].class));
        }
    }

    @SuppressWarnings("EmptyMethod")
    public static synchronized void create() {
    }
}
