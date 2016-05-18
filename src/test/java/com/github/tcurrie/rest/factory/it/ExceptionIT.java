package com.github.tcurrie.rest.factory.it;

import com.github.tcurrie.rest.factory.Strings;
import com.github.tcurrie.rest.factory.client.RestClientFactory;
import com.github.tcurrie.rest.factory.it.apis.TestApi;
import com.github.tcurrie.rest.factory.it.impls.TestService;
import com.github.tcurrie.rest.factory.v1.RestFactoryException;
import com.openpojo.random.RandomFactory;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class ExceptionIT {

    private TestApi client;

    @Before
    public void before() {
        this.client = RestClientFactory.create(TestApi.class, ()->RestServers.SERVER.getUrl() + "/spring-generated-rest");
    }

    @Test
    public void testThrowsException() {
        final Exception expected = new Exception(RandomFactory.getRandomValue(String.class));
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
            expected = RestFactoryException.create(Strings.format("Failed to adapt exception [{}] from response.", Strings.getStackTrace(unbuildable)), e);
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
}
