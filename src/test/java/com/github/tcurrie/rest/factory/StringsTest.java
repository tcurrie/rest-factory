package com.github.tcurrie.rest.factory;

import com.openpojo.random.RandomFactory;
import org.junit.Test;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class StringsTest {

    @Test
    public void testStringsFormat() {
        final String expected = RandomFactory.getRandomValue(String.class);
        assertThat(Strings.format("{}", expected), is(expected));
    }

    @Test
    public void testStringFormatWithNoArgs() {
        final String expected = RandomFactory.getRandomValue(String.class);
        assertThat(Strings.format(expected), is(expected));
    }

    @Test
    public void testStringFormatWithNullArgs() {
        final String expected = RandomFactory.getRandomValue(String.class);
        assertThat(Strings.format(expected, null), is(expected));
    }

    @Test
    public void testStringFormatWithNullMessage() {
        final String expected = RandomFactory.getRandomValue(String.class);
        assertThat(Strings.format(null, expected), is("[" + expected + "]"));
    }

    @Test
    public void testStringFormatWithNullMessageAndNoArgs() {
        final String expected = RandomFactory.getRandomValue(String.class);
        assertThat(Strings.format(null), is("[]"));
    }
    @Test
    public void testStringFormatWithNullMessageAndNullArgs() {
        final String expected = RandomFactory.getRandomValue(String.class);
        assertThat(Strings.format(null, null), is("null"));
    }

    @Test
    public void testStringFormatWithTooFewParameters() {
        final String expected = RandomFactory.getRandomValue(String.class);
        assertThat(Strings.format("{},{}", expected), is(expected + ",{}"));
    }

    @Test
    public void testStringFormatWithTooManyParameters() {
        final String expected = RandomFactory.getRandomValue(String.class);
        assertThat(Strings.format("", expected), is("Message[], parameters[[" + expected + "]]. Format failed."));
    }

    @Test
    public void testStringFormatFailed() {
        class UnStringable {
            @Override
            public String toString() {
                throw new RuntimeException(RandomFactory.getRandomValue(String.class));
            }
        }
        final UnStringable unStringable = new UnStringable();
        assertThat(Strings.format("{}", unStringable), is("[FAILED toString()]"));

    }

    @Test
    public void shouldGetStackTrace() {
        final Throwable t = new RuntimeException(RandomFactory.getRandomValue(String.class));
        final StringWriter stackTrace = new StringWriter();
        t.printStackTrace(new PrintWriter(stackTrace));
        final String expected = stackTrace.toString();

        assertThat(Strings.getStackTrace(t), is(expected));
    }

}
