package com.github.tcurrie.rest.factory.it;

import com.github.tcurrie.rest.factory.client.RestClientFactory;
import com.github.tcurrie.rest.factory.it.apis.PojoRandomGenerator;
import com.github.tcurrie.rest.factory.it.apis.TestApi;
import com.openpojo.random.RandomFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class OverloadIT {
    private TestApi client;

    @Before
    public void before() {
        PojoRandomGenerator.create();
        this.client = RestClientFactory.create(TestApi.class, ()->RestServers.SERVER.getUrl() + "/generated-rest");
    }

    @Test
    public void testFunctionOverloadCallsCorrectImplementation() {
        final String[] input = RandomFactory.getRandomValue(String[].class);
        final String separator = RandomFactory.getRandomValue(String.class);
        final String expectedA = Arrays.stream(input).collect(Collectors.joining());
        final String expectedB = Arrays.stream(input).collect(Collectors.joining(separator));

        final String actualA = client.join(input);
//        final String actualB = client.join(input, separator);

        assertThat(actualA, is(expectedA));
//        assertThat(actualB, is(expectedB));
    }
}
