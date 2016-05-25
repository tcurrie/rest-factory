package com.github.tcurrie.rest.factory.it;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tcurrie.rest.factory.RestResponseAdaptor;
import com.github.tcurrie.rest.factory.Strings;
import com.github.tcurrie.rest.factory.client.HTTPExchange;
import com.github.tcurrie.rest.factory.client.RestClientFactory;
import com.github.tcurrie.rest.factory.it.apis.Pojo;
import com.github.tcurrie.rest.factory.it.apis.TestApi;
import com.github.tcurrie.rest.factory.it.impls.TestService;
import com.github.tcurrie.rest.factory.v1.RestFactoryException;
import com.openpojo.random.RandomFactory;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import static com.github.tcurrie.rest.factory.client.HTTPExchange.Method.ECHO;
import static com.github.tcurrie.rest.factory.client.HTTPExchange.Method.POST;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class ExceptionIT {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private TestApi client;

    @Before
    public void before() {
        this.client = RestClientFactory.create(TestApi.class, ()->RestServers.SERVER.getUrl() + "/generated-rest");
    }

    @Test
    public void testThrowsException() {
        final Exception expected = new IllegalStateException(RandomFactory.getRandomValue(String.class));
        TestService.DATA.put("exception", expected);

        try {
            final int result = client.throwsException();
            fail("Should have thrown exception, got[" + result  +"]");
        } catch (final Exception actual) {
            assertThat(actual, CoreMatchers.instanceOf(expected.getClass()));
            assertThat(actual.getMessage(), is(expected.getMessage()));
            assertThat(actual.getCause().getMessage(), containsString(Strings.getStackTrace(expected)));
        }
    }

    @Test
    public void testThrowsRuntimeException() {
        final Exception expected = new RuntimeException(RandomFactory.getRandomValue(String.class),
                new RuntimeException(RandomFactory.getRandomValue(String.class)));
        TestService.DATA.put("runtimeException", expected);

        try {
            final int result = client.throwsRuntimeException();
            fail("Should have thrown exception, got[" + result  +"]");
        } catch (final RuntimeException actual) {
            assertThat(actual, CoreMatchers.instanceOf(expected.getClass()));
            assertThat(actual.getMessage(), is(expected.getMessage()));
            assertThat(actual.getCause().getMessage(), containsString(Strings.getStackTrace(expected)));
        }
    }

    @Test
    public void testThrowsUnbuildableExceptionType() {
        final class Unbuildable extends RuntimeException {
            private Unbuildable() { super(RandomFactory.getRandomValue(String.class)); }
        }
        final Exception unbuildable = new Unbuildable();
        TestService.DATA.put("runtimeException", unbuildable);
        final RestFactoryException expected;
        try {
            Unbuildable.class.getConstructor(String.class, Throwable.class);
            throw new RuntimeException();
        } catch (NoSuchMethodException e) {
            //noinspection ThrowableResultOfMethodCallIgnored
            expected = new RestFactoryException(Strings.format("Failed to adapt exception [{}] from response.", Strings.getStackTrace(unbuildable)), e);
        }

        try {
            final int result = client.throwsRuntimeException();
            fail("Should have thrown exception, got[" + result  +"]");
        } catch (final RestFactoryException actual) {
            assertThat(actual, CoreMatchers.instanceOf(expected.getClass()));
            assertThat(actual.getMessage(), is(expected.getMessage()));
            assertThat(actual.getCause().getMessage(), is(expected.getCause().getMessage()));
        }
    }

    @Test
    public void testThrowsExceptionForUnknownMethod() throws Throwable {
        final Method method = TestApi.class.getMethod("consumer", Pojo.class);
        final Pojo expected = RandomFactory.getRandomValue(Pojo.class);
        TestService.DATA.put("consumed", RandomFactory.getRandomValue(Pojo.class));

        final String methodUrl = RestServers.SERVER.getUrl() + "/generated-rest/test-api/v1/unknown";
        final String parameters = MAPPER.writeValueAsString(expected);

        final String result = HTTPExchange.execute(methodUrl, parameters, POST, 30, TimeUnit.SECONDS);

        try {
            RestResponseAdaptor.Client.Factory.create(method).apply(result);
            fail("Should have thrown exception, got[" + result  +"]");
        } catch (final RestFactoryException e) {
            assertThat(e.getMessage(), startsWith("Failed to match request [/generated-rest/test-api/v1/unknown] to any Handler from [["));
        }
    }

    @Test
    public void testEchoThrowsExceptionForInvalidParameters() throws Throwable {
        TestService.DATA.put("consumed", RandomFactory.getRandomValue(Pojo.class));
        final Method method = TestApi.class.getMethod("consumer", Pojo.class);

        final String methodUrl = RestServers.SERVER.getUrl() + "/generated-rest/test-api/v1/consumer";
        final String parameters = MAPPER.writeValueAsString("invalid");

        final String result = HTTPExchange.execute(methodUrl, parameters, ECHO, 30, TimeUnit.SECONDS);

        try {
            RestResponseAdaptor.Client.Factory.create(method).apply(result);
            fail("Should have thrown exception, got[" + result  +"]");
        } catch (final RestFactoryException e) {
            assertThat(e.getMessage(), is("Failed to read arguments, got [[null]]."));
        }
    }


}
