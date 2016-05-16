package com.github.tcurrie.rest.factory;

import com.openpojo.random.RandomFactory;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

public class StringsTest {

    @Test
    public void testStringsFormat() {
        final String expected = RandomFactory.getRandomValue(String.class);
        Assert.assertThat(Strings.format("{}", expected), CoreMatchers.is(expected));
    }
}
