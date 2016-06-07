package com.github.tcurrie.rest.factory.client;

import com.github.tcurrie.rest.factory.v1.TimeOut;
import com.openpojo.random.RandomFactory;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static org.hamcrest.CoreMatchers.is;

public class RestClientFactoryTest {

    @Test
    public void testGetsClientUrlSupplier() {
        final String url = RandomFactory.getRandomValue(String.class);
        //noinspection unchecked
        final Supplier<String> urlSupplier = Mockito.mock(Supplier.class);
        Mockito.when(urlSupplier.get()).thenReturn(url);
        final Blah client = RestClientFactory.create(Blah.class, urlSupplier, ()-> TimeOut.create(RandomFactory.getRandomValue(long.class), RandomFactory.getRandomValue(TimeUnit.class)));

        final Supplier<String> actualSupplier = RestClientFactory.getUrlSupplier(client);

        Mockito.verifyZeroInteractions(urlSupplier);
        Assert.assertThat(actualSupplier.get(), is(url));
        Mockito.verify(urlSupplier).get();
    }
    @Test
    public void testGetsClientTimoutSupplier() {
        final TimeOut timeOut = TimeOut.create(RandomFactory.getRandomValue(long.class), RandomFactory.getRandomValue(TimeUnit.class));
        //noinspection unchecked
        final Supplier<TimeOut> timeOutSupplier = Mockito.mock(Supplier.class);
        Mockito.when(timeOutSupplier.get()).thenReturn(timeOut);
        final Blah client = RestClientFactory.create(Blah.class, ()->RandomFactory.getRandomValue(String.class), timeOutSupplier);

        final Supplier<TimeOut> actualSupplier = RestClientFactory.getTimeoutSupplier(client);

        Mockito.verifyZeroInteractions(timeOutSupplier);
        Assert.assertThat(actualSupplier.get(), is(timeOut));
        Mockito.verify(timeOutSupplier).get();
    }

    @SuppressWarnings("unused")
    private interface Blah {
        void blah();
    }
}
