package com.github.tcurrie.rest.factory.it;

import com.github.tcurrie.rest.factory.client.RestClientFactory;
import com.github.tcurrie.rest.factory.it.apis.Pojo;
import com.github.tcurrie.rest.factory.it.apis.TestApi;
import com.github.tcurrie.rest.factory.it.impls.TestService;
import com.google.common.collect.Lists;
import com.openpojo.random.RandomFactory;
import com.openpojo.random.RandomGenerator;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

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
        Assert.assertThat(TestService.DATA.get("runs"), nullValue());
        client.runnable();
        Assert.assertThat(TestService.DATA.get("runs"), is(1));
    }

    @Test
    public void testConsumesPojo() {
        final Pojo expected = RandomFactory.getRandomValue(Pojo.class);

        Assert.assertThat(TestService.DATA.get("consumed"), nullValue());
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
}
