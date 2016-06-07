package com.github.tcurrie.rest.factory.proxy;

import com.github.tcurrie.rest.factory.Strings;
import com.github.tcurrie.rest.factory.v1.RestFactoryException;

import java.lang.reflect.Field;

@SuppressWarnings("WeakerAccess")
public class Fields {
    private Fields() {
        throw new RestFactoryException("Can not construct instance of Factory class.");
    }
    public static <T, U> T getField(final U instance, final String fieldName) {
        try {
            final Field field = instance.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            //noinspection unchecked
            return (T) field.get(instance);
        } catch (final Exception e) {
            throw new RestFactoryException(Strings.format("Invalid field name [{}].", fieldName), e);
        }
    }
}
