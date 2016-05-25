package com.github.tcurrie.rest.factory.it;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tcurrie.rest.factory.RestUriFactory;
import com.github.tcurrie.rest.factory.client.RestClientFactory;
import com.github.tcurrie.rest.factory.it.apis.Pojo;
import com.github.tcurrie.rest.factory.it.apis.PojoRandomGenerator;
import com.github.tcurrie.rest.factory.it.apis.TestApi;
import com.github.tcurrie.rest.factory.it.impls.TestService;
import com.github.tcurrie.rest.factory.proxy.Methods;
import com.github.tcurrie.rest.factory.proxy.ProxyMethodHandler;
import com.github.tcurrie.rest.factory.v1.RestClientMonitor;
import com.github.tcurrie.rest.factory.v1.RestMethod;
import com.github.tcurrie.rest.factory.v1.RestMethodDictionary;
import com.github.tcurrie.rest.factory.v1.RestMethodVerificationResult;
import com.google.common.collect.Lists;
import com.openpojo.random.RandomFactory;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ProducerIT {

    private static final Supplier<String> URL_SUPPLIER = () -> RestServers.SERVER.getUrl() + "/generated-rest";
    private TestApi client;

    @Before
    public void before() {
        PojoRandomGenerator.create();
        this.client = RestClientFactory.create(TestApi.class, URL_SUPPLIER);
    }

    @Test
    public void testProducesPojo() {
        final Pojo expected = RandomFactory.getRandomValue(Pojo.class);
        TestService.DATA.put("produce", expected);
        final Pojo actual = client.producer();
        assertThat(actual, is(expected));
    }

    @Test
    public void testProducesVerificationResults() {
        final Map<String, String> expected = Stream.of(
                RestClientFactory.create(TestApi.class, URL_SUPPLIER),
                RestClientFactory.create(RestClientMonitor.class, URL_SUPPLIER))
                .flatMap(c->Methods.BeanFactory.map(c, t->m->m))
                .filter(m->!ProxyMethodHandler.class.equals(m.getDeclaringClass()))
                .collect(Collectors.toMap(
                        Method::toGenericString,
                        m->RestUriFactory.getInstance().create(URL_SUPPLIER, m.getDeclaringClass(), m).get()
                        ));

        final RestClientMonitor monitor = RestClientFactory.create(RestClientMonitor.class, ()->RestServers.SERVER.getUrl() + "/generated-rest");
        final Set<RestMethodVerificationResult> actual = monitor.verifyClients();
        actual.stream().sorted((a, b)->a.getUrl().compareTo(b.getUrl())).forEach(t->{
            if (t.getUrl().startsWith("http")) {
                assertThat(t.isSuccess(), is(true));
                assertThat(t.getArgs(), is(t.getResult()));
                if (expected.containsKey(t.getApi())) {
                    assertThat(t.getUrl(), is(expected.get(t.getApi())));
                    expected.remove(t.getApi());
                }
            } else {
                assertThat(t.isSuccess(), is(false));
                assertThat(t.getException(), CoreMatchers.notNullValue());
            }
        });
        assertThat("Should have found [" + expected + "]", expected.size(), is(0));
    }

    @Test
    public void testGetRestMethodDictionary() throws IOException {
        final JavaType type = new ObjectMapper().getTypeFactory().constructParametrizedType(Set.class, HashSet.class,
                RestMethod.class);
        final String expectedJson =
        "[{\"uri\":\"/test-api/v1/add\",\"method\":\"add\",\"bean\":\"com.github.tcurrie.rest.factory.it.apis.TestApi\"},{\"uri\":\"/rest-client-monitor/v1/verify-clients\",\"method\":\"verifyClients\",\"bean\":\"com.github.tcurrie.rest.factory.v1.RestClientMonitor\"},{\"uri\":\"/rest-method-dictionary/v1/get-methods\",\"method\":\"getMethods\",\"bean\":\"com.github.tcurrie.rest.factory.v1.RestMethodDictionary\"},{\"uri\":\"/test-api/v1/throws-exception\",\"method\":\"throwsException\",\"bean\":\"com.github.tcurrie.rest.factory.it.apis.TestApi\"},{\"uri\":\"/test-api/v1/producer\",\"method\":\"producer\",\"bean\":\"com.github.tcurrie.rest.factory.it.apis.TestApi\"},{\"uri\":\"/test-api/v1/runnable\",\"method\":\"runnable\",\"bean\":\"com.github.tcurrie.rest.factory.it.apis.TestApi\"},{\"uri\":\"/test-api/v1/consumer\",\"method\":\"consumer\",\"bean\":\"com.github.tcurrie.rest.factory.it.apis.TestApi\"},{\"uri\":\"/test-api/v1/throws-runtime-exception\",\"method\":\"throwsRuntimeException\",\"bean\":\"com.github.tcurrie.rest.factory.it.apis.TestApi\"},{\"uri\":\"/test-api/v1/reverse\",\"method\":\"reverse\",\"bean\":\"com.github.tcurrie.rest.factory.it.apis.TestApi\"},{\"uri\":\"/test-api/v1/min\",\"method\":\"min\",\"bean\":\"com.github.tcurrie.rest.factory.it.apis.TestApi\"},{\"uri\":\"/test-api/v1/join\",\"method\":\"join\",\"bean\":\"com.github.tcurrie.rest.factory.it.apis.TestApi\"},{\"uri\":\"/test-api/v1/dedup\",\"method\":\"dedup\",\"bean\":\"com.github.tcurrie.rest.factory.it.apis.TestApi\"},{\"uri\":\"/test-api/v1/concatenate\",\"method\":\"concatenate\",\"bean\":\"com.github.tcurrie.rest.factory.it.apis.TestApi\"},{\"uri\":\"/test-api/v1/sum\",\"method\":\"sum\",\"bean\":\"com.github.tcurrie.rest.factory.it.apis.TestApi\"}]";
        //"[{\"uri\":\"/test-api/v1/add\",\"method\":\"add\",\"bean\":\"com.github.tcurrie.rest.factory.it.apis.TestApi\"},{\"uri\":\"/rest-client-monitor/v1/verify-clients\",\"method\":\"verifyClients\",\"bean\":\"com.github.tcurrie.rest.factory.v1.RestClientMonitor\"},{\"uri\":\"/rest-method-dictionary/v1/get-methods\",\"method\":\"getMethods\",\"bean\":\"com.github.tcurrie.rest.factory.v1.RestMethodDictionary\"},{\"uri\":\"/test-api/v1/throws-exception\",\"method\":\"throwsException\",\"bean\":\"com.github.tcurrie.rest.factory.it.apis.TestApi\"},{\"uri\":\"/test-api/v1/producer\",\"method\":\"producer\",\"bean\":\"com.github.tcurrie.rest.factory.it.apis.TestApi\"},{\"uri\":\"/test-api/v1/consumer\",\"method\":\"consumer\",\"bean\":\"com.github.tcurrie.rest.factory.it.apis.TestApi\"},{\"uri\":\"/test-api/v1/runnable\",\"method\":\"runnable\",\"bean\":\"com.github.tcurrie.rest.factory.it.apis.TestApi\"},{\"uri\":\"/test-api/v1/throws-runtime-exception\",\"method\":\"throwsRuntimeException\",\"bean\":\"com.github.tcurrie.rest.factory.it.apis.TestApi\"},{\"uri\":\"/test-api/v1/reverse\",\"method\":\"reverse\",\"bean\":\"com.github.tcurrie.rest.factory.it.apis.TestApi\"},{\"uri\":\"/test-api/v1/min\",\"method\":\"min\",\"bean\":\"com.github.tcurrie.rest.factory.it.apis.TestApi\"},{\"uri\":\"/test-api/v1/dedup\",\"method\":\"dedup\",\"bean\":\"com.github.tcurrie.rest.factory.it.apis.TestApi\"},{\"uri\":\"/test-api/v1/concatenate\",\"method\":\"concatenate\",\"bean\":\"com.github.tcurrie.rest.factory.it.apis.TestApi\"},{\"uri\":\"/test-api/v1/sum\",\"method\":\"sum\",\"bean\":\"com.github.tcurrie.rest.factory.it.apis.TestApi\"}]";
        final Set<RestMethod> expected = new ObjectMapper().readValue(expectedJson, type);

        final RestMethodDictionary dictionary = RestClientFactory.create(RestMethodDictionary.class, ()->RestServers.SERVER.getUrl() + "/generated-rest");
        final Set<RestMethod> actual = dictionary.getMethods();

        assertThat(actual, is(expected));

        final List<RestMethod> sortedExpected = Lists.newArrayList(expected);
        Collections.sort(sortedExpected, (o1, o2) -> o1.getUri().compareTo(o2.getUri()));

        final List<RestMethod> sortedActual = Lists.newArrayList(actual);
        Collections.sort(sortedActual, (o1, o2) -> o1.getUri().compareTo(o2.getUri()));

        for (int i = 0; i < sortedActual.size(); i++) {
            assertThat(sortedActual.get(i), is(sortedExpected.get(i)));
            assertThat(sortedActual.get(i).getMethod(), is(sortedExpected.get(i).getMethod()));
            assertThat(sortedActual.get(i).getBean(), is(sortedExpected.get(i).getBean()));
        }
    }

}
