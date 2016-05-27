package com.github.tcurrie.rest.factory.it;

import com.github.tcurrie.rest.factory.it.apis.Pojo;
import com.github.tcurrie.rest.factory.it.apis.PojoRandomGenerator;
import com.google.common.collect.Sets;
import com.openpojo.random.RandomFactory;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class FunctionIT {
    @Before
    public void before() {
        PojoRandomGenerator.create();
    }

    @Test
    public void testFunctionAppliesPojoToPojo() {
        final Pojo input = RandomFactory.getRandomValue(Pojo.class);
        int[] copy = ArrayUtils.clone(input.getData());
        ArrayUtils.reverse(copy);
        final Pojo expected = new Pojo(StringUtils.reverse(input.getValue()), copy);

        final Pojo actual = TestClients.getValidTestApi().reverse(input);

        assertThat(actual, is(expected));
    }

    @Test
    public void testFunctionAppliesTwoPojosToPojo() {
        final Pojo a = RandomFactory.getRandomValue(Pojo.class);
        final Pojo b = RandomFactory.getRandomValue(Pojo.class);

        final Pojo expected = new Pojo(a.getValue() + " " + b.getValue(), ArrayUtils.addAll(a.getData(), b.getData()));

        final Pojo actual = TestClients.getValidTestApi().concatenate(a, b);

        assertThat(actual, is(expected));
    }

    @Test
    public void testFunctionAppliesTwoIntsToInt() {
        final int a = RandomFactory.getRandomValue(int.class);
        final int b = RandomFactory.getRandomValue(int.class);

        final int expected = a + b;

        final int actual = TestClients.getValidTestApi().add(a, b);

        assertThat(actual, is(expected));
    }

    @Test
    public void testFunctionAppliesVarArgInts() {
        final int a = RandomFactory.getRandomValue(int.class);
        final int b = RandomFactory.getRandomValue(int.class);
        final int c = RandomFactory.getRandomValue(int.class);

        final int expected = a + b + c;

        final int actual = TestClients.getValidTestApi().sum(a, b, c);

        assertThat(actual, is(expected));
    }

    @Test
    public void testPojoVarArgToSet() {
        final Pojo a = RandomFactory.getRandomValue(Pojo.class);
        final Pojo b = RandomFactory.getRandomValue(Pojo.class);
        final Set<Pojo> expected = Sets.newHashSet(a, b);

        final Set<Pojo> actual = TestClients.getValidTestApi().dedup(a, b, a, b);

        assertThat(actual, is(expected));
    }

    @Test
    public void testPojoSetToPojo() {
        final Pojo a = RandomFactory.getRandomValue(Pojo.class);
        final Pojo b = RandomFactory.getRandomValue(Pojo.class);
        final Pojo expected = a.getValue().compareTo(b.getValue()) < 0 ? a : b;

        final Pojo actual = TestClients.getValidTestApi().min(Sets.newHashSet(a, b));

        assertThat(actual, is(expected));
    }
}
