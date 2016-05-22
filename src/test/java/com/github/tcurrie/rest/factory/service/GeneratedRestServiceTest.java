package com.github.tcurrie.rest.factory.service;

import com.github.tcurrie.rest.factory.v1.RestFactoryException;
import com.openpojo.random.RandomFactory;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class GeneratedRestServiceTest {
    @Test
    public void testWrapsIOExceptionOnWrite() throws IOException {
        final GeneratedRestService restService = new GeneratedRestService();
        final HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        final String result = RandomFactory.getRandomValue(String.class);
        final IOException ioException = new IOException(RandomFactory.getRandomValue(String.class));
        Mockito.doThrow(ioException).when(response).getWriter();
        try {
            restService.writeResult(response, result, HttpServletResponse.SC_OK);
            fail("Expected RestFactoryException.");
        } catch (final RestFactoryException e) {
            assertThat(e.getMessage(), is("Failed to write response [" + result + "]."));
            assertThat(e.getCause(), is(ioException));
        }
    }

}
