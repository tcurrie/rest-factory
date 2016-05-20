package com.github.tcurrie.rest.factory;

import com.github.tcurrie.rest.factory.it.apis.Pojo;
import com.github.tcurrie.rest.factory.it.apis.PojoRandomGenerator;
import com.github.tcurrie.rest.factory.it.apis.TestApi;
import com.github.tcurrie.rest.factory.v1.RestFactoryException;
import com.openpojo.random.RandomFactory;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

public class RestParameterAdaptorTest {

    @Before
    public void before() {
        PojoRandomGenerator.create();
    }

    @Test
    public void testClientAndServerAdaptorsAreSymmetrical() throws NoSuchMethodException {
        final Method method = TestApi.class.getMethod("dedup", Pojo[].class);
        final Pojo[] expected = RandomFactory.getRandomValue(Pojo[].class);
        final String json = RestParameterAdaptor.Client.Factory.create(method).apply(new Object[]{expected});
        final Pojo[] actual = (Pojo[]) RestParameterAdaptor.Service.Factory.create(method).apply(json)[0];

        Assert.assertArrayEquals(expected, actual);
    }

    @Test
    public void testFailToMapData() throws NoSuchMethodException {
        final Looper looper = new Looper();
        looper.setLooper(looper);

        final Method method = Looper.class.getMethod("setLooper", Looper.class);

        try {
            RestParameterAdaptor.Client.Factory.create(method).apply(new Object[]{looper});
        } catch (final RestFactoryException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.startsWith("Failed to map [[" + looper.toString() + "]]"));
        }
    }

    @Test
    public void testFailToReacdData() throws NoSuchMethodException {
        final Method method = TestApi.class.getMethod("dedup", Pojo[].class);

        try {
            RestParameterAdaptor.Service.Factory.create(method).apply("");
        } catch (final RestFactoryException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.startsWith("Failed to read arguments, got [[null]]."));
        }
    }
}
