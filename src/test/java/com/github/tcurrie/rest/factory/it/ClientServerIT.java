package com.github.tcurrie.rest.factory.it;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tcurrie.rest.factory.Strings;
import com.github.tcurrie.rest.factory.client.RestClientFactory;
import com.github.tcurrie.rest.factory.it.apis.Pojo;
import com.github.tcurrie.rest.factory.it.apis.TestApi;
import com.github.tcurrie.rest.factory.it.impls.TestService;
import com.github.tcurrie.rest.factory.model.RestFactoryException;
import com.github.tcurrie.rest.factory.service.RestMethodDictionary;
import com.github.tcurrie.rest.factory.service.RestMethodDictionary.MethodDescription;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.openpojo.random.RandomFactory;
import com.openpojo.random.RandomGenerator;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class ClientServerIT extends WebDriverTestBasis {

    private TestApi client;

    @Before
    public void before() {
        RandomFactory.addRandomGenerator(new RandomGenerator() {
            @Override
            public Collection<Class<?>> getTypes() {
                return Lists.newArrayList(Pojo.class);
            }

            @Override
            public Object doGenerate(final Class<?> type) {
                return new Pojo(RandomFactory.getRandomValue(String.class), RandomFactory.getRandomValue(int[].class));
            }
        });
        client = RestClientFactory.create(TestApi.class, ()->RestServers.SERVER.getUrl() + "/spring-generated-rest");
    }

    @Test
    public void testRuns() {
        assertThat(TestService.DATA.get("runs"), nullValue());
        client.runnable();
        assertThat(TestService.DATA.get("runs"), is(1));
    }

    @Test
    public void testConsumesPojo() {
        final Pojo expected = RandomFactory.getRandomValue(Pojo.class);

        assertThat(TestService.DATA.get("consumed"), nullValue());
        client.consumer(expected);
        assertThat(TestService.DATA.get("consumed"), is(expected));
    }

    @Test
    public void testProducesPojo() {
        final Pojo expected = RandomFactory.getRandomValue(Pojo.class);
        TestService.DATA.put("produce", expected);
        final Pojo actual = client.producer();
        assertThat(actual, is(expected));
    }

    @Test
    public void testFunctionAppliesPojoToPojo() {
        final Pojo input = RandomFactory.getRandomValue(Pojo.class);
        int[] copy = ArrayUtils.clone(input.getData());
        ArrayUtils.reverse(copy);
        final Pojo expected = new Pojo(StringUtils.reverse(input.getValue()), copy);

        final Pojo actual = client.reverse(input);

        assertThat(actual, is(expected));
    }

    @Test
    public void testFunctionAppliesTwoPojosToPojo() {
        final Pojo a = RandomFactory.getRandomValue(Pojo.class);
        final Pojo b = RandomFactory.getRandomValue(Pojo.class);

        final Pojo expected = new Pojo(a.getValue() + " " + b.getValue(), ArrayUtils.addAll(a.getData(), b.getData()));

        final Pojo actual = client.concatenate(a, b);

        assertThat(actual, is(expected));
    }

    @Test
    public void testFunctionAppliesTwoIntsToInt() {
        final int a = RandomFactory.getRandomValue(int.class);
        final int b = RandomFactory.getRandomValue(int.class);

        final int expected = a + b;

        final int actual = client.add(a, b);

        assertThat(actual, is(expected));
    }

    @Test
    public void testFunctionAppliesVarArgInts() {
        final int a = RandomFactory.getRandomValue(int.class);
        final int b = RandomFactory.getRandomValue(int.class);
        final int c = RandomFactory.getRandomValue(int.class);

        final int expected = a + b + c;

        final int actual = client.sum(a, b, c);

        assertThat(actual, is(expected));
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
            assertThat(actual.getCause().getMessage(), containsString(getStackTrace(expected)));
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
            assertThat(actual.getCause().getMessage(), containsString(getStackTrace(expected)));
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

    @Test
    public void testPojoVarArgToSet() {
        final Pojo a = RandomFactory.getRandomValue(Pojo.class);
        final Pojo b = RandomFactory.getRandomValue(Pojo.class);
        final Set<Pojo> expected = Sets.newHashSet(a, b);

        final Set<Pojo> actual = client.dedup(a, b, a, b);

        assertThat(actual, is(expected));
    }

    @Test
    public void testPojoSetToPojo() {
        final Pojo a = RandomFactory.getRandomValue(Pojo.class);
        final Pojo b = RandomFactory.getRandomValue(Pojo.class);
        final Pojo expected = a.getValue().compareTo(b.getValue()) < 0 ? a : b;

        final Pojo actual = client.min(Sets.newHashSet(a, b));

        assertThat(actual, is(expected));
    }

    @Test
    public void testGetRestMethodDictionary() throws IOException {
        final JavaType type = new ObjectMapper().getTypeFactory().constructParametrizedType(Set.class, HashSet.class,
                MethodDescription.class);
        final String expectedJson =
        "[{\"uri\":\"/test-api/v1/add\",\"method\":\"add\",\"bean\":\"com.github.tcurrie.rest.factory.it.impls.TestService\"},{\"uri\":\"/test-api/v1/min\",\"method\":\"min\",\"bean\":\"com.github.tcurrie.rest.factory.it.impls.TestService\"},{\"uri\":\"/rest-method-dictionary/v1/get-methods\",\"method\":\"getMethods\",\"bean\":\"com.github.tcurrie.rest.factory.service.UriSetRestHandlerDictionary\"},{\"uri\":\"/test-api/v1/throws-exception\",\"method\":\"throwsException\",\"bean\":\"com.github.tcurrie.rest.factory.it.impls.TestService\"},{\"uri\":\"/test-api/v1/producer\",\"method\":\"producer\",\"bean\":\"com.github.tcurrie.rest.factory.it.impls.TestService\"},{\"uri\":\"/test-api/v1/runnable\",\"method\":\"runnable\",\"bean\":\"com.github.tcurrie.rest.factory.it.impls.TestService\"},{\"uri\":\"/test-api/v1/consumer\",\"method\":\"consumer\",\"bean\":\"com.github.tcurrie.rest.factory.it.impls.TestService\"},{\"uri\":\"/test-api/v1/dedup\",\"method\":\"dedup\",\"bean\":\"com.github.tcurrie.rest.factory.it.impls.TestService\"},{\"uri\":\"/test-api/v1/concatenate\",\"method\":\"concatenate\",\"bean\":\"com.github.tcurrie.rest.factory.it.impls.TestService\"},{\"uri\":\"/test-api/v1/sum\",\"method\":\"sum\",\"bean\":\"com.github.tcurrie.rest.factory.it.impls.TestService\"},{\"uri\":\"/test-api/v1/throws-runtime-exception\",\"method\":\"throwsRuntimeException\",\"bean\":\"com.github.tcurrie.rest.factory.it.impls.TestService\"},{\"uri\":\"/test-api/v1/reverse\",\"method\":\"reverse\",\"bean\":\"com.github.tcurrie.rest.factory.it.impls.TestService\"}]";
         //       "[{\"uri\":\"/test-api/v1/add\",\"method\":\"add\",\"bean\":\"com.github.tcurrie.rest.factory.it.impls.TestService\"},{\"uri\":\"/rest-method-dictionary/v1/get-methods\",\"method\":\"getMethods\",\"bean\":\"com.github.tcurrie.rest.factory.service.UriSetRestHandlerDictionary\"},{\"uri\":\"/test-api/v1/throws-exception\",\"method\":\"throwsException\",\"bean\":\"com.github.tcurrie.rest.factory.it.impls.TestService\"},{\"uri\":\"/test-api/v1/producer\",\"method\":\"producer\",\"bean\":\"com.github.tcurrie.rest.factory.it.impls.TestService\"},{\"uri\":\"/test-api/v1/runnable\",\"method\":\"runnable\",\"bean\":\"com.github.tcurrie.rest.factory.it.impls.TestService\"},{\"uri\":\"/test-api/v1/consumer\",\"method\":\"consumer\",\"bean\":\"com.github.tcurrie.rest.factory.it.impls.TestService\"},{\"uri\":\"/test-api/v1/dedup\",\"method\":\"dedup\",\"bean\":\"com.github.tcurrie.rest.factory.it.impls.TestService\"},{\"uri\":\"/test-api/v1/concatenate\",\"method\":\"concatenate\",\"bean\":\"com.github.tcurrie.rest.factory.it.impls.TestService\"},{\"uri\":\"/test-api/v1/sum\",\"method\":\"sum\",\"bean\":\"com.github.tcurrie.rest.factory.it.impls.TestService\"},{\"uri\":\"/test-api/v1/throws-runtime-exception\",\"method\":\"throwsRuntimeException\",\"bean\":\"com.github.tcurrie.rest.factory.it.impls.TestService\"},{\"uri\":\"/test-api/v1/reverse\",\"method\":\"reverse\",\"bean\":\"com.github.tcurrie.rest.factory.it.impls.TestService\"}]";
                //"[{\"uri\":\"/test-api/v1/add\",\"method\":\"add\",\"bean\":\"com.github.tcurrie.rest.factory.it.impls.TestService\"},{\"uri\":\"/rest-method-dictionary/v1/get-methods\",\"method\":\"getMethods\",\"bean\":\"com.github.tcurrie.rest.factory.service.UriSetRestHandlerDictionary\"},{\"uri\":\"/test-api/v1/throws-exception\",\"method\":\"throwsException\",\"bean\":\"com.github.tcurrie.rest.factory.it.impls.TestService\"},{\"uri\":\"/test-api/v1/producer\",\"method\":\"producer\",\"bean\":\"com.github.tcurrie.rest.factory.it.impls.TestService\"},{\"uri\":\"/test-api/v1/runnable\",\"method\":\"runnable\",\"bean\":\"com.github.tcurrie.rest.factory.it.impls.TestService\"},{\"uri\":\"/test-api/v1/consumer\",\"method\":\"consumer\",\"bean\":\"com.github.tcurrie.rest.factory.it.impls.TestService\"},{\"uri\":\"/test-api/v1/concatenate\",\"method\":\"concatenate\",\"bean\":\"com.github.tcurrie.rest.factory.it.impls.TestService\"},{\"uri\":\"/test-api/v1/sum\",\"method\":\"sum\",\"bean\":\"com.github.tcurrie.rest.factory.it.impls.TestService\"},{\"uri\":\"/test-api/v1/throws-runtime-exception\",\"method\":\"throwsRuntimeException\",\"bean\":\"com.github.tcurrie.rest.factory.it.impls.TestService\"},{\"uri\":\"/test-api/v1/reverse\",\"method\":\"reverse\",\"bean\":\"com.github.tcurrie.rest.factory.it.impls.TestService\"}]";
        final Set<MethodDescription> expected = new ObjectMapper().readValue(expectedJson, type);

        final RestMethodDictionary dictionary = RestClientFactory.create(RestMethodDictionary.class, ()->RestServers.SERVER.getUrl() + "/spring-generated-rest");
        final Set<MethodDescription> actual = dictionary.getMethods();

        assertThat(actual, is(expected));

        final List<MethodDescription> sortedExpected = Lists.newArrayList(expected);
        Collections.sort(sortedExpected, (o1, o2) -> o1.getUri().compareTo(o2.getUri()));

        final List<MethodDescription> sortedActual = Lists.newArrayList(actual);
        Collections.sort(sortedActual, (o1, o2) -> o1.getUri().compareTo(o2.getUri()));

        for (int i = 0; i < sortedActual.size(); i++) {
            assertThat(sortedActual.get(i), is(sortedExpected.get(i)));
            assertThat(sortedActual.get(i).getMethod(), is(sortedExpected.get(i).getMethod()));
            assertThat(sortedActual.get(i).getBean(), is(sortedExpected.get(i).getBean()));
        }
    }
    private String getStackTrace(final Exception expected) {
        final StringWriter stack = new StringWriter();
        expected.printStackTrace(new PrintWriter(stack));
        return stack.toString();
    }
}
