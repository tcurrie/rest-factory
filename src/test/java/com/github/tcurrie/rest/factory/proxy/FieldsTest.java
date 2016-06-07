package com.github.tcurrie.rest.factory.proxy;

import com.github.tcurrie.rest.factory.v1.RestFactoryException;
import com.openpojo.random.RandomFactory;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;

public class FieldsTest {
    @Test
    public void testGetsFieldByName() {
        final int expected = RandomFactory.getRandomValue(int.class);
        final A bean = new A();
        bean.setB(expected);
        Assert.assertThat(Fields.getField(bean, "b"), is(expected));
    }

    @Test
    public void testGetsExceptionForNoSuchField() {
        final int expected = RandomFactory.getRandomValue(int.class);
        final A bean = new A();
        bean.setB(expected);

        final String fieldName = RandomFactory.getRandomValue(String.class);
        try {
            Fields.getField(bean, fieldName);
            Assert.fail("Should have thrown exception.");
        } catch (final RestFactoryException e) {
            Assert.assertThat(e.getMessage(), is("Invalid field name [" + fieldName + "]."));
            Assert.assertThat(e.getCause(), instanceOf(NoSuchFieldException.class));
        }
    }

    @SuppressWarnings("unused")
    private static class A {
        private int b;

        private void setB(final int b) {
            this.b = b;
        }
    }
}
