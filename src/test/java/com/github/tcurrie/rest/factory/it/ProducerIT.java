package com.github.tcurrie.rest.factory.it;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tcurrie.rest.factory.RestUriFactory;
import com.github.tcurrie.rest.factory.client.RestClientFactory;
import com.github.tcurrie.rest.factory.it.apis.Pojo;
import com.github.tcurrie.rest.factory.it.apis.PojoRandomGenerator;
import com.github.tcurrie.rest.factory.it.impls.TestService;
import com.github.tcurrie.rest.factory.proxy.Methods;
import com.github.tcurrie.rest.factory.v1.RestMethod;
import com.github.tcurrie.rest.factory.v1.RestMethodDictionary;
import com.github.tcurrie.rest.factory.v1.RestMethodVerificationResult;
import com.github.tcurrie.rest.factory.v1.TimeOut;
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
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ProducerIT {


    @Before
    public void before() {
        PojoRandomGenerator.create();
    }

    @Test
    public void testProducesPojo() {
        final Pojo expected = RandomFactory.getRandomValue(Pojo.class);
        TestService.DATA.put("produce", expected);
        final Pojo actual = TestClients.getValidTestApi().producer();
        assertThat(actual, is(expected));
    }

    @Test
    public void testProducesVerificationResults() {
        final Function<Supplier<String>, Function<Method, String>> toSig =
                s -> m -> RestUriFactory.getInstance().create(s, m.getDeclaringClass(), m).get()
                + "\n" + m.toGenericString();

        final Stream<String> validTestApi = Methods.BeanFactory.map(TestClients.getValidTestApi(), t -> m -> toSig.apply(TestClients.URL_SUPPLIER).apply(m));
        final Stream<String> invalidTestApi = Methods.BeanFactory.map(TestClients.getInvalidTestApi(), t -> m -> toSig.apply(TestClients.INVALID_URL_SUPPLIER).apply(m));
        final Stream<String> restClientMethod = Methods.BeanFactory.map(TestClients.getRestClientMonitor(), t -> m -> toSig.apply(TestClients.URL_SUPPLIER).apply(m));
        final Stream<String> restMethodDictionary = Methods.BeanFactory.map(TestClients.getRestMethodDictionary(), t -> m -> toSig.apply(TestClients.URL_SUPPLIER).apply(m));

        final Set<String> expected = Stream.concat(validTestApi, Stream.concat(invalidTestApi, Stream.concat(restClientMethod, restMethodDictionary)))
                .filter(s->!s.contains("ProxyMethodHandler"))
                .collect(Collectors.toSet());

        final Set<RestMethodVerificationResult> actual = RestClientFactory.verify(TestClients.URL_SUPPLIER.get(), TimeOut.create(30, TimeUnit.SECONDS));
        actual.stream().sorted((a, b)->a.getUrl().compareTo(b.getUrl())).forEach(t->{
            final String signature = t.getUrl() + "\n" + t.getApi();
            if (expected.contains(signature)) {
                if (t.getUrl().startsWith("http")) {
                    assertThat(t.isSuccess(), is(true));
                    assertThat(t.getArgs(), is(t.getResult()));
                } else {
                    assertThat(t.isSuccess(), is(false));
                    assertThat(t.getException(), CoreMatchers.notNullValue());
                }
                expected.remove(signature);
            }
        });
        assertThat("Should have found [" + expected + "]", expected.size(), is(0));
    }

    @Test
    public void testGetRestMethodDictionary() throws IOException {
        final JavaType type = new ObjectMapper().getTypeFactory().constructParametrizedType(Set.class, HashSet.class,
                RestMethod.class);
        final String expectedJson =
        "[{\"uri\":\"http://localhost:9090/generated-rest/test-api/v1/reverse\",\"api\":\"public abstract com.github.tcurrie.rest.factory.it.apis.Pojo com.github.tcurrie.rest.factory.it.apis.TestApi.reverse(com.github.tcurrie.rest.factory.it.apis.Pojo)\"},{\"uri\":\"http://localhost:9090/generated-rest/test-api/v1/runnable\",\"api\":\"public abstract void com.github.tcurrie.rest.factory.it.apis.TestApi.runnable()\"},{\"uri\":\"http://localhost:9090/generated-rest/test-api/v1/concatenate\",\"api\":\"public abstract com.github.tcurrie.rest.factory.it.apis.Pojo com.github.tcurrie.rest.factory.it.apis.TestApi.concatenate(com.github.tcurrie.rest.factory.it.apis.Pojo,com.github.tcurrie.rest.factory.it.apis.Pojo)\"},{\"uri\":\"http://localhost:9090/generated-rest/test-api/v1/join\",\"api\":\"public abstract java.lang.String com.github.tcurrie.rest.factory.it.apis.TestApi.join(java.lang.String[])\"},{\"uri\":\"http://localhost:9090/generated-rest/rest-client-monitor/v1/verify-clients\",\"api\":\"public abstract java.util.Set<com.github.tcurrie.rest.factory.v1.RestMethodVerificationResult> com.github.tcurrie.rest.factory.v1.RestClientMonitor.verifyClients()\"},{\"uri\":\"http://localhost:9090/generated-rest/test-api/v1/producer\",\"api\":\"public abstract com.github.tcurrie.rest.factory.it.apis.Pojo com.github.tcurrie.rest.factory.it.apis.TestApi.producer()\"},{\"uri\":\"http://localhost:9090/generated-rest/test-api/v1/add\",\"api\":\"public abstract int com.github.tcurrie.rest.factory.it.apis.TestApi.add(int,int)\"},{\"uri\":\"http://localhost:9090/generated-rest/test-api/v1/min\",\"api\":\"public abstract com.github.tcurrie.rest.factory.it.apis.Pojo com.github.tcurrie.rest.factory.it.apis.TestApi.min(java.util.Set<com.github.tcurrie.rest.factory.it.apis.Pojo>)\"},{\"uri\":\"http://localhost:9090/generated-rest/rest-method-dictionary/v1/get-methods\",\"api\":\"public abstract java.util.Set<com.github.tcurrie.rest.factory.v1.RestMethod> com.github.tcurrie.rest.factory.v1.RestMethodDictionary.getMethods()\"},{\"uri\":\"http://localhost:9090/generated-rest/test-api/v1/consumer\",\"api\":\"public abstract void com.github.tcurrie.rest.factory.it.apis.TestApi.consumer(com.github.tcurrie.rest.factory.it.apis.Pojo)\"},{\"uri\":\"http://localhost:9090/generated-rest/test-api/v1/join\",\"api\":\"public abstract java.lang.String com.github.tcurrie.rest.factory.it.apis.TestApi.join(java.lang.String[],java.lang.String)\"},{\"uri\":\"http://localhost:9090/generated-rest/test-api/v1/sum\",\"api\":\"public abstract int com.github.tcurrie.rest.factory.it.apis.TestApi.sum(int...)\"},{\"uri\":\"http://localhost:9090/generated-rest/test-api/v1/throws-exception\",\"api\":\"public abstract int com.github.tcurrie.rest.factory.it.apis.TestApi.throwsException() throws java.lang.Exception\"},{\"uri\":\"http://localhost:9090/generated-rest/test-api/v1/dedup\",\"api\":\"public abstract java.util.Set<com.github.tcurrie.rest.factory.it.apis.Pojo> com.github.tcurrie.rest.factory.it.apis.TestApi.dedup(com.github.tcurrie.rest.factory.it.apis.Pojo...)\"},{\"uri\":\"http://localhost:9090/generated-rest/test-api/v1/throws-runtime-exception\",\"api\":\"public abstract int com.github.tcurrie.rest.factory.it.apis.TestApi.throwsRuntimeException()\"}]";
        //"[{\"uri\":\"/test-api/v1/join\",\"api\":\"public abstract java.lang.String com.github.tcurrie.rest.factory.it.apis.TestApi.join(java.lang.String[],java.lang.String)\"},{\"uri\":\"/test-api/v1/throws-runtime-exception\",\"api\":\"public abstract int com.github.tcurrie.rest.factory.it.apis.TestApi.throwsRuntimeException()\"},{\"uri\":\"/test-api/v1/add\",\"api\":\"public abstract int com.github.tcurrie.rest.factory.it.apis.TestApi.add(int,int)\"},{\"uri\":\"/test-api/v1/producer\",\"api\":\"public abstract com.github.tcurrie.rest.factory.it.apis.Pojo com.github.tcurrie.rest.factory.it.apis.TestApi.producer()\"},{\"uri\":\"/rest-method-dictionary/v1/get-methods\",\"api\":\"public abstract java.util.Set<com.github.tcurrie.rest.factory.v1.RestMethod> com.github.tcurrie.rest.factory.v1.RestMethodDictionary.getMethods()\"},{\"uri\":\"/test-api/v1/sum\",\"api\":\"public abstract int com.github.tcurrie.rest.factory.it.apis.TestApi.sum(int...)\"},{\"uri\":\"/test-api/v1/reverse\",\"api\":\"public abstract com.github.tcurrie.rest.factory.it.apis.Pojo com.github.tcurrie.rest.factory.it.apis.TestApi.reverse(com.github.tcurrie.rest.factory.it.apis.Pojo)\"},{\"uri\":\"/test-api/v1/throws-exception\",\"api\":\"public abstract int com.github.tcurrie.rest.factory.it.apis.TestApi.throwsException() throws java.lang.Exception\"},{\"uri\":\"/test-api/v1/consumer\",\"api\":\"public abstract void com.github.tcurrie.rest.factory.it.apis.TestApi.consumer(com.github.tcurrie.rest.factory.it.apis.Pojo)\"},{\"uri\":\"/test-api/v1/dedup\",\"api\":\"public abstract java.util.Set<com.github.tcurrie.rest.factory.it.apis.Pojo> com.github.tcurrie.rest.factory.it.apis.TestApi.dedup(com.github.tcurrie.rest.factory.it.apis.Pojo...)\"},{\"uri\":\"/test-api/v1/min\",\"api\":\"public abstract com.github.tcurrie.rest.factory.it.apis.Pojo com.github.tcurrie.rest.factory.it.apis.TestApi.min(java.util.Set<com.github.tcurrie.rest.factory.it.apis.Pojo>)\"},{\"uri\":\"/test-api/v1/concatenate\",\"api\":\"public abstract com.github.tcurrie.rest.factory.it.apis.Pojo com.github.tcurrie.rest.factory.it.apis.TestApi.concatenate(com.github.tcurrie.rest.factory.it.apis.Pojo,com.github.tcurrie.rest.factory.it.apis.Pojo)\"},{\"uri\":\"/rest-client-monitor/v1/verify-clients\",\"api\":\"public abstract java.util.Set<com.github.tcurrie.rest.factory.v1.RestMethodVerificationResult> com.github.tcurrie.rest.factory.v1.RestClientMonitor.verifyClients()\"},{\"uri\":\"/test-api/v1/runnable\",\"api\":\"public abstract void com.github.tcurrie.rest.factory.it.apis.TestApi.runnable()\"},{\"uri\":\"/test-api/v1/join\",\"api\":\"public abstract java.lang.String com.github.tcurrie.rest.factory.it.apis.TestApi.join(java.lang.String[])\"}]";
     //   "[{\"uri\":\"/test-api/v1/add\",\"method\":\"add\",\"bean\":\"com.github.tcurrie.rest.factory.it.apis.TestApi\"},{\"uri\":\"/rest-client-monitor/v1/verify-clients\",\"method\":\"verifyClients\",\"bean\":\"com.github.tcurrie.rest.factory.v1.RestClientMonitor\"},{\"uri\":\"/rest-method-dictionary/v1/get-methods\",\"method\":\"getMethods\",\"bean\":\"com.github.tcurrie.rest.factory.v1.RestMethodDictionary\"},{\"uri\":\"/test-api/v1/throws-exception\",\"method\":\"throwsException\",\"bean\":\"com.github.tcurrie.rest.factory.it.apis.TestApi\"},{\"uri\":\"/test-api/v1/producer\",\"method\":\"producer\",\"bean\":\"com.github.tcurrie.rest.factory.it.apis.TestApi\"},{\"uri\":\"/test-api/v1/runnable\",\"method\":\"runnable\",\"bean\":\"com.github.tcurrie.rest.factory.it.apis.TestApi\"},{\"uri\":\"/test-api/v1/consumer\",\"method\":\"consumer\",\"bean\":\"com.github.tcurrie.rest.factory.it.apis.TestApi\"},{\"uri\":\"/test-api/v1/throws-runtime-exception\",\"method\":\"throwsRuntimeException\",\"bean\":\"com.github.tcurrie.rest.factory.it.apis.TestApi\"},{\"uri\":\"/test-api/v1/reverse\",\"method\":\"reverse\",\"bean\":\"com.github.tcurrie.rest.factory.it.apis.TestApi\"},{\"uri\":\"/test-api/v1/min\",\"method\":\"min\",\"bean\":\"com.github.tcurrie.rest.factory.it.apis.TestApi\"},{\"uri\":\"/test-api/v1/join\",\"method\":\"join\",\"bean\":\"com.github.tcurrie.rest.factory.it.apis.TestApi\"},{\"uri\":\"/test-api/v1/dedup\",\"method\":\"dedup\",\"bean\":\"com.github.tcurrie.rest.factory.it.apis.TestApi\"},{\"uri\":\"/test-api/v1/concatenate\",\"method\":\"concatenate\",\"bean\":\"com.github.tcurrie.rest.factory.it.apis.TestApi\"},{\"uri\":\"/test-api/v1/sum\",\"method\":\"sum\",\"bean\":\"com.github.tcurrie.rest.factory.it.apis.TestApi\"}]";
        final Set<RestMethod> expected = new ObjectMapper().readValue(expectedJson, type);

        final RestMethodDictionary dictionary = TestClients.getRestMethodDictionary();
        final Set<RestMethod> actual = dictionary.getMethods();

        assertThat(actual, is(expected));

        final List<RestMethod> sortedExpected = Lists.newArrayList(expected);
        Collections.sort(sortedExpected, (o1, o2) -> o1.getUri().compareTo(o2.getUri()));

        final List<RestMethod> sortedActual = Lists.newArrayList(actual);
        Collections.sort(sortedActual, (o1, o2) -> o1.getUri().compareTo(o2.getUri()));

        for (int i = 0; i < sortedActual.size(); i++) {
            assertThat(sortedActual.get(i), is(sortedExpected.get(i)));
            assertThat(sortedActual.get(i).getApi(), is(sortedExpected.get(i).getApi()));
        }
    }

}
