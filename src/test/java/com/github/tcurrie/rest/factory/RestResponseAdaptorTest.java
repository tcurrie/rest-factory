package com.github.tcurrie.rest.factory;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.github.tcurrie.rest.factory.it.apis.Pojo;
import com.github.tcurrie.rest.factory.it.apis.PojoRandomGenerator;
import com.github.tcurrie.rest.factory.it.apis.TestApi;
import com.github.tcurrie.rest.factory.v1.RestFactoryException;
import com.openpojo.random.RandomFactory;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Method;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class RestResponseAdaptorTest {
    @Before
    public void before() {
        PojoRandomGenerator.create();
    }

    @Test
    public void testClientAndServiceAreSymmetrical() throws Throwable {
        final Method method = TestApi.class.getMethod("producer");
        final RestResponseAdaptor.Client<Pojo> client = RestResponseAdaptor.Client.Factory.create(method);
        final RestResponseAdaptor.Service<Pojo> service = RestResponseAdaptor.Service.Factory.create();
        final Pojo expected = RandomFactory.getRandomValue(Pojo.class);

        final String response = service.apply(expected);
        final Pojo actual = client.apply(response);

        assertThat(actual, is(expected));
    }

    @Test
    public void testClientInstanceFailsToAdaptResult() throws Throwable {
        final Method method = TestApi.class.getMethod("producer");
        final RestResponseAdaptor.Client<Pojo> client = RestResponseAdaptor.Client.Factory.create(method);
        final String expected = RandomFactory.getRandomValue(String.class);

        try {
            client.apply(expected);
            fail();
        } catch (final RestFactoryException e) {
            assertThat(e.getMessage(), is("Failed to adapt result [" + expected + "] from response."));
            assertThat(e.getCause(), instanceOf(IOException.class));
        }
    }

    @Test
    public void testClientInstanceDelegatesFailureResultToExceptionAdaptor() throws Throwable {
        final RuntimeException expected = new RuntimeException(RandomFactory.getRandomValue(String.class));

        final Method method = TestApi.class.getMethod("producer");
        final RestResponseAdaptor.Client<Pojo> client = RestResponseAdaptor.Client.Factory.create(method);


        final String json = RestResponseAdaptor.Service.THROWABLE.apply(expected);
        System.out.println(json);
        try {
            client.apply(json);
            fail();
        } catch (final RuntimeException actual) {
            assertThat(actual.getMessage(), is(expected.getMessage()));
        }
    }

    @Test
    public void testServiceInstanceFailsToAdaptResult() throws NoSuchMethodException {
        final RestResponseAdaptor.Service<Looper> service = RestResponseAdaptor.Service.Factory.create();
        final Looper looper = new Looper();
        looper.setLooper(looper);

        try {
            service.apply(looper);
            fail();
        } catch (final RestFactoryException e) {
            assertThat(e.getMessage(), is("Failed to adapt result [" + looper + "] to response."));
            assertThat(e.getCause(), instanceOf(JsonMappingException.class));
        }
    }


}
