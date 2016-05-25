package com.github.tcurrie.rest.factory.it;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import com.github.tcurrie.rest.factory.v1.RestMethodVerificationResult;
import com.openpojo.random.RandomFactory;
import com.openpojo.reflection.PojoField;
import com.openpojo.reflection.impl.PojoClassFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import static com.github.tcurrie.rest.factory.client.HTTPExchange.Method.DELETE;
import static com.github.tcurrie.rest.factory.client.HTTPExchange.Method.ECHO;
import static com.github.tcurrie.rest.factory.client.HTTPExchange.Method.GET;
import static com.github.tcurrie.rest.factory.client.HTTPExchange.Method.POST;
import static com.github.tcurrie.rest.factory.client.HTTPExchange.Method.PUT;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class ConsumerIT {
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private static final PojoField CONNECTION_METHOD = PojoClassFactory.getPojoClass(HttpURLConnection.class).getPojoFields().stream().filter(f -> f.getName().equals("method")).findFirst().get();
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final TypeFactory TYPE_FACTORY = MAPPER.getTypeFactory();
    private TestApi client;

    @Before
    public void before() {
        PojoRandomGenerator.create();
        this.client = RestClientFactory.create(TestApi.class, ()->RestServers.SERVER.getUrl() + "/generated-rest");
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
        TestService.DATA.put("consumed", RandomFactory.getRandomValue(Pojo.class));
        try {
            RestClientFactory.create(TestApi.class, ()->"invalid").consumer(unexpected);
            fail();
        } catch (final RestFactoryException e) {
            assertThat(e.getMessage(), startsWith("Failed to execute HTTPExchange, url[invalid/test-api/v1/consumer]"));
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
    public void testConsumesPojoWithOptions() throws IOException {
        final String methodUrl = RestServers.SERVER.getUrl() + "/generated-rest/test-api/v1/consumer";
        final Pojo expected = RandomFactory.getRandomValue(Pojo.class);

        verifyConnectionResults("OPTIONS", methodUrl, expected, (connection, result) -> {
            assertThat(result, is(""));
            assertThat(connection.getHeaderField("Allow"), is("GET, POST, PUT, DELETE, ECHO, OPTIONS"));
        });
    }

    @Test
    public void testConsumesPojoWithEcho() throws IOException {
        final Pojo expected = RandomFactory.getRandomValue(Pojo.class);
        TestService.DATA.put("consumed", RandomFactory.getRandomValue(Pojo.class));

        final String body = exchange(ECHO, expected);

        final ResponseWrapper<Pojo[]> wrapper = adaptResponse(body, Pojo[].class);

        Assert.assertNotNull(wrapper);
        Assert.assertThat(wrapper.isSuccess(), is(true));
        Assert.assertArrayEquals(new Pojo[]{expected}, wrapper.getResult());
        Assert.assertNull(wrapper.getException());

        assertThat(TestService.DATA.get("consumed"), not(expected));
    }

    @Test
    public void testVerifiesClient() {
        final Set<RestMethodVerificationResult> verified = RestClientFactory.verify(client);
        verified.forEach(m -> {
            Assert.assertThat(m.isSuccess(), is(true));
            Assert.assertArrayEquals(m.getArgs(), m.getResult());
        });
    }

    private void verifyConnectionResults(final String method, final String methodUrl, final Pojo expected, final BiConsumer<HttpURLConnection, String> v) throws IOException {
        final String parameters = MAPPER.writeValueAsString(expected);
        HttpURLConnection connection = (HttpURLConnection) new URL(methodUrl).openConnection();
        connection.setDoOutput(true);
        connection.setUseCaches(false);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Content-Length", Integer.toString(parameters.getBytes().length));
        connection.setRequestProperty("Content-Language", "en-US");

        try (final DataOutputStream out = new DataOutputStream(connection.getOutputStream())) {
            out.writeBytes(parameters);
        }
        CONNECTION_METHOD.set(connection, method);

        final InputStream is = connection.getResponseCode() == HttpServletResponse.SC_OK ? connection.getInputStream() : connection.getErrorStream();

        try (final BufferedReader in = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            final String result = in.lines().collect(Collectors.joining("\n"));
            v.accept(connection, result);
        } finally {
            connection.disconnect();
        }
    }

    private void tryMethod(final HTTPExchange.Method method) throws IOException {
        final Pojo expected = RandomFactory.getRandomValue(Pojo.class);

        final String body = exchange(method, expected);

        final ResponseWrapper<Object> wrapper = adaptResponse(body, Object.class);

        Assert.assertNotNull(wrapper);
        Assert.assertThat(wrapper.isSuccess(), is(true));
        Assert.assertNull(wrapper.getResult());
        Assert.assertNull(wrapper.getException());

        assertThat(TestService.DATA.get("consumed"), is(expected));
    }


    private String exchange(final HTTPExchange.Method method, final Pojo expected) throws JsonProcessingException {
        final String methodUrl = RestServers.SERVER.getUrl() + "/generated-rest/test-api/v1/consumer";
        final String parameters = MAPPER.writeValueAsString(expected);
        return HTTPExchange.execute(methodUrl, parameters, method, 30, TimeUnit.SECONDS);
    }

    private <T> ResponseWrapper<T> adaptResponse(final String body, final Class<T> type) throws IOException {
        return MAPPER.readValue(body, TYPE_FACTORY.constructParametrizedType(ResponseWrapper.class, ResponseWrapper.class, type));
    }

}
