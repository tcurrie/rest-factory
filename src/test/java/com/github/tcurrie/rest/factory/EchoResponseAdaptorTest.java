package com.github.tcurrie.rest.factory;

import com.github.tcurrie.rest.factory.it.apis.Pojo;
import com.github.tcurrie.rest.factory.it.apis.TestApi;
import com.github.tcurrie.rest.factory.proxy.Methods;
import com.github.tcurrie.rest.factory.v1.RestMethodVerificationResult;
import com.openpojo.random.RandomFactory;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Method;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class EchoResponseAdaptorTest {

    @Test
    public void shouldAdaptFailureResult() throws IOException {
        final Method method = Methods.TypeFactory.get(TestApi.class, "concatenate", Pojo.class, Pojo.class);
        final String url = RandomFactory.getRandomValue(String.class);
        final String api = method.toGenericString();
        final Object[] args = RandomFactory.getRandomValue(String[].class);
        final Throwable throwable = new IllegalArgumentException(RandomFactory.getRandomValue(String.class));

        final RestMethodVerificationResult expected = RestMethodVerificationResult.createFailure(url, api, args, throwable);
        final String response = RestResponseAdaptor.Service.THROWABLE.apply(throwable);

        final RestMethodVerificationResult actual = EchoResponseAdaptor.Client.Factory.create(method).apply(url, args, response);

        assertThat(actual, is(expected));
        assertThat(actual.isSuccess(), is(expected.isSuccess()));
        assertThat(actual.getArgs(), is(expected.getArgs()));
        assertThat(actual.getResult(), is(expected.getResult()));
        assertThat(actual.getException(), instanceOf(expected.getException().getClass()));
        assertThat(actual.getException().getMessage(), is(expected.getException().getMessage()));
    }

}
