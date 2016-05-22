package com.github.tcurrie.rest.factory.client;

import com.github.tcurrie.rest.factory.v1.RestFactoryException;
import com.openpojo.random.RandomFactory;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class TimeBoxedTest {

    @Test
    public void testCompletesWork() {
        final boolean complete = TimeBoxed.attempt(()->true, 1, TimeUnit.MINUTES);
        assertThat(complete, is(true));
    }

    @Test
    public void testThrowsException() {
        final RuntimeException expected = new RuntimeException(RandomFactory.getRandomValue(String.class));
        try {
            TimeBoxed.attempt(()-> {
                throw expected;
            }, 1, TimeUnit.MINUTES);
            fail();
        } catch (final RuntimeException e) {
            assertThat(e, is(expected));
        }
    }

    @Test
    public void testTimesOut() {
        try {
            TimeBoxed.attempt(()-> {
                try {
                    Thread.sleep(Integer.MAX_VALUE);
                } catch (InterruptedException e) {
                    throw new RuntimeException();
                }
                return true;
            }, 1, TimeUnit.MILLISECONDS);
            fail();
        } catch (final RestFactoryException e) {
            assertThat(e.getMessage(), is("Failed to complete task."));
            assertThat(e.getCause(), instanceOf(TimeoutException.class));
        }
    }

}
