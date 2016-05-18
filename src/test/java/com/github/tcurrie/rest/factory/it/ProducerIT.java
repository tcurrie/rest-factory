package com.github.tcurrie.rest.factory.it;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tcurrie.rest.factory.client.RestClientFactory;
import com.github.tcurrie.rest.factory.it.apis.Pojo;
import com.github.tcurrie.rest.factory.it.apis.PojoRandomGenerator;
import com.github.tcurrie.rest.factory.it.apis.TestApi;
import com.github.tcurrie.rest.factory.it.impls.TestService;
import com.github.tcurrie.rest.factory.v1.RestMethodDictionary;
import com.github.tcurrie.rest.factory.v1.RestMethodDictionary.MethodDescription;
import com.google.common.collect.Lists;
import com.openpojo.random.RandomFactory;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ProducerIT {

    private TestApi client;

    @Before
    public void before() {
        PojoRandomGenerator.create();
        this.client = RestClientFactory.create(TestApi.class, ()->RestServers.SERVER.getUrl() + "/spring-generated-rest");
    }

    @Test
    public void testProducesPojo() {
        final Pojo expected = RandomFactory.getRandomValue(Pojo.class);
        TestService.DATA.put("produce", expected);
        final Pojo actual = client.producer();
        assertThat(actual, is(expected));
    }

    @Test
    public void testGetRestMethodDictionary() throws IOException {
        final JavaType type = new ObjectMapper().getTypeFactory().constructParametrizedType(Set.class, HashSet.class,
                MethodDescription.class);
        final String expectedJson =
        "[{\"uri\":\"/test-api/v1/add\",\"method\":\"add\",\"bean\":\"com.github.tcurrie.rest.factory.it.impls.TestService\"},{\"uri\":\"/test-api/v1/min\",\"method\":\"min\",\"bean\":\"com.github.tcurrie.rest.factory.it.impls.TestService\"},{\"uri\":\"/rest-method-dictionary/v1/get-methods\",\"method\":\"getMethods\",\"bean\":\"com.github.tcurrie.rest.factory.service.UriSetRestHandlerDictionary\"},{\"uri\":\"/test-api/v1/throws-exception\",\"method\":\"throwsException\",\"bean\":\"com.github.tcurrie.rest.factory.it.impls.TestService\"},{\"uri\":\"/test-api/v1/producer\",\"method\":\"producer\",\"bean\":\"com.github.tcurrie.rest.factory.it.impls.TestService\"},{\"uri\":\"/test-api/v1/runnable\",\"method\":\"runnable\",\"bean\":\"com.github.tcurrie.rest.factory.it.impls.TestService\"},{\"uri\":\"/test-api/v1/consumer\",\"method\":\"consumer\",\"bean\":\"com.github.tcurrie.rest.factory.it.impls.TestService\"},{\"uri\":\"/test-api/v1/dedup\",\"method\":\"dedup\",\"bean\":\"com.github.tcurrie.rest.factory.it.impls.TestService\"},{\"uri\":\"/test-api/v1/concatenate\",\"method\":\"concatenate\",\"bean\":\"com.github.tcurrie.rest.factory.it.impls.TestService\"},{\"uri\":\"/test-api/v1/sum\",\"method\":\"sum\",\"bean\":\"com.github.tcurrie.rest.factory.it.impls.TestService\"},{\"uri\":\"/test-api/v1/throws-runtime-exception\",\"method\":\"throwsRuntimeException\",\"bean\":\"com.github.tcurrie.rest.factory.it.impls.TestService\"},{\"uri\":\"/test-api/v1/reverse\",\"method\":\"reverse\",\"bean\":\"com.github.tcurrie.rest.factory.it.impls.TestService\"}]";
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

}
