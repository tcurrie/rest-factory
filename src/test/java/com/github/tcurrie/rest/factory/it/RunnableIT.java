package com.github.tcurrie.rest.factory.it;

import com.github.tcurrie.rest.factory.it.impls.TestService;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class RunnableIT {
    @Test
    public void testRuns() {
        assertThat(TestService.DATA.get("runs"), nullValue());
        TestClients.getValidTestApi().runnable();
        assertThat(TestService.DATA.get("runs"), is(1));
    }
}
