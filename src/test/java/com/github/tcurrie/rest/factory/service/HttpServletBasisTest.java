package com.github.tcurrie.rest.factory.service;

import com.github.tcurrie.rest.factory.RestResponseAdaptor;
import com.github.tcurrie.rest.factory.it.apis.Pojo;
import com.github.tcurrie.rest.factory.it.apis.TestApi;
import com.github.tcurrie.rest.factory.v1.RestFactoryException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class HttpServletBasisTest {

    @Test
    public void shouldWriteExceptionForNonHttpProtocol() throws Throwable {
        final AtomicBoolean serviced = new AtomicBoolean();
        final StringWriter writer = new StringWriter();
        final Method method = TestApi.class.getMethod("consumer", Pojo.class);
        final HttpServletBasis servlet = new HttpServletBasis() {
            @Override
            protected void service(final HttpServletRequest request, final HttpServletResponse response) {
                serviced.set(true);
            }
        };

        final ServletRequest request = Mockito.mock(ServletRequest.class);
        final ServletResponse response = Mockito.mock(ServletResponse.class);
        Mockito.when(request.getProtocol()).thenReturn("abcd");
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(writer));

        servlet.service(request, response);
        final String result = writer.getBuffer().toString();

        Assert.assertThat(serviced.get(), CoreMatchers.is(false));

        try {
            RestResponseAdaptor.Client.Factory.create(method).apply(result);
            fail("Should have thrown exception, got[" + result  +"]");
        } catch (final RestFactoryException e) {
            assertThat(e.getMessage(), is("Invalid request protocol [abcd].  Accepts [http]."));
        }
    }
}
