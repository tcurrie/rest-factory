package com.github.tcurrie.rest.factory;

import com.github.tcurrie.rest.factory.v1.RestFactoryException;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class JsonAdaptorTest {

    @Test
    public void shouldFailForNoDefaultConstructor() throws NoSuchMethodException {
        final Method method = Unbuildable.class.getMethod("unAdaptable", Unbuildable.class);
        try {
            JsonAdaptor.Factory.create(method);
            fail();
        } catch (final RestFactoryException e) {
            assertThat(e.getMessage(), is("Can not wire adaptor for parameter [class com.github.tcurrie.rest.factory.JsonAdaptorTest$Unbuildable] type has no default constructor."));
        }
    }

    private static final class Unbuildable {
        @SuppressWarnings("UnusedParameters")
        private Unbuildable(final int unused) {

        }
        @SuppressWarnings({"unused", "WeakerAccess"})
        public void unAdaptable(final Unbuildable u) {
        }
    }
}
