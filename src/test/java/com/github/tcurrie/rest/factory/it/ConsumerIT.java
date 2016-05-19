package com.github.tcurrie.rest.factory.it;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.github.tcurrie.rest.factory.client.HTTPExchange;
import com.github.tcurrie.rest.factory.client.RestClientFactory;
import com.github.tcurrie.rest.factory.it.apis.Pojo;
import com.github.tcurrie.rest.factory.it.apis.PojoRandomGenerator;
import com.github.tcurrie.rest.factory.it.apis.TestApi;
import com.github.tcurrie.rest.factory.it.impls.TestService;
import com.github.tcurrie.rest.factory.v1.ResponseWrapper;
import com.github.tcurrie.rest.factory.v1.RestFactoryException;
import com.openpojo.random.RandomFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static com.github.tcurrie.rest.factory.client.HTTPExchange.Method.DELETE;
import static com.github.tcurrie.rest.factory.client.HTTPExchange.Method.ECHO;
import static com.github.tcurrie.rest.factory.client.HTTPExchange.Method.GET;
import static com.github.tcurrie.rest.factory.client.HTTPExchange.Method.POST;
import static com.github.tcurrie.rest.factory.client.HTTPExchange.Method.PUT;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class ConsumerIT {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final TypeFactory TYPE_FACTORY = MAPPER.getTypeFactory();
    private TestApi client;
    private AtomicReference<String> url = new AtomicReference<>(RestServers.SERVER.getUrl() + "/spring-generated-rest");

    @Before
    public void before() {
        PojoRandomGenerator.create();
        this.client = RestClientFactory.create(TestApi.class, ()->url.get());
    }

    @Test
    public void testConsumesPojo() {
        final Pojo expected = RandomFactory.getRandomValue(Pojo.class);
        client.consumer(expected);
        assertThat(TestService.DATA.get("consumed"), is(expected));
    }

    @Test
    public void testThrowsExceptionWhenClientFails() throws IOException {
        final Pojo unexpected = RandomFactory.getRandomValue(Pojo.class);
        url.set("invalid");
        TestService.DATA.put("consumed", RandomFactory.getRandomValue(Pojo.class));
        try {
            client.consumer(unexpected);
            fail();
        } catch (final RestFactoryException e) {
            assertThat(e.getMessage(), is("Failed to complete task."));
            assertThat(e.getCause().getCause(), instanceOf(RestFactoryException.class));
            assertThat(e.getCause().getCause().getMessage(), startsWith("Failed to execute HTTPExchange, url[invalid/test-api/v1/consumer]"));
        }
        assertThat(TestService.DATA.get("consumed"), not(unexpected));

    }

    @Test
    public void testConsumesPojoWithGet() throws IOException {
        tryMethod(GET);
    }
    @Test
    public void testConsumesPojoWithPost() throws IOException {
        tryMethod(POST);
    }

    @Test
    public void testConsumesPojoWithPut() throws IOException {
        tryMethod(PUT);
    }

    @Test
    public void testConsumesPojoWithDelete() throws IOException {
        tryMethod(DELETE);
    }


    @Test
    public void testConsumesPojoWithEcho() throws IOException {
        final Pojo expected = RandomFactory.getRandomValue(Pojo.class);
        TestService.DATA.put("consumed", RandomFactory.getRandomValue(Pojo.class));

        final String methodUrl = RestServers.SERVER.getUrl() + "/spring-generated-rest/test-api/v1/consumer";
        final String parameters = MAPPER.writeValueAsString(expected);

        final String body = HTTPExchange.execute(methodUrl, parameters, ECHO, 30, TimeUnit.SECONDS);

        final JavaType type = TYPE_FACTORY.constructParametrizedType(ResponseWrapper.class, ResponseWrapper.class, Pojo[].class);
        final ResponseWrapper<Pojo[]> wrapper = MAPPER.readValue(body, type);

        Assert.assertNotNull(wrapper);
        Assert.assertThat(wrapper.isSuccess(), is(true));
        Assert.assertArrayEquals(new Pojo[]{expected}, wrapper.getResult());
        Assert.assertNull(wrapper.getException());

        assertThat(TestService.DATA.get("consumed"), not(expected));
    }


    private void tryMethod(final HTTPExchange.Method method) throws IOException {
        final Pojo expected = RandomFactory.getRandomValue(Pojo.class);

        final String methodUrl = RestServers.SERVER.getUrl() + "/spring-generated-rest/test-api/v1/consumer";
        final String parameters = MAPPER.writeValueAsString(expected);

        final String body = HTTPExchange.execute(methodUrl, parameters, method, 30, TimeUnit.SECONDS);

        final JavaType type = TYPE_FACTORY.constructParametrizedType(ResponseWrapper.class, ResponseWrapper.class, Object.class);
        final ResponseWrapper<Object> wrapper = MAPPER.readValue(body, type);

        Assert.assertNotNull(wrapper);
        Assert.assertThat(wrapper.isSuccess(), is(true));
        Assert.assertNull(wrapper.getResult());
        Assert.assertNull(wrapper.getException());

        assertThat(TestService.DATA.get("consumed"), is(expected));
    }
}
