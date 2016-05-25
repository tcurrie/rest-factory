package com.github.tcurrie.rest.factory.it;

import com.github.tcurrie.rest.factory.client.RestClientFactory;
import com.github.tcurrie.rest.factory.it.apis.PojoRandomGenerator;
import com.github.tcurrie.rest.factory.it.apis.TestApi;
import com.github.tcurrie.rest.factory.it.impls.TestService;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class RunnableIT {

    private TestApi client;

    @Before
    public void before() {
        PojoRandomGenerator.create();
        this.client = RestClientFactory.create(TestApi.class, ()->RestServers.SERVER.getUrl() + "/generated-rest");
    }

    @Test
    public void testRuns() {
        assertThat(TestService.DATA.get("runs"), nullValue());
        client.runnable();
        assertThat(TestService.DATA.get("runs"), is(1));
    }
}
