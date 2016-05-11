package com.github.tcurrie.rest.factory.it.apis;

import com.github.tcurrie.rest.factory.client.RestClientFactory;
import com.github.tcurrie.rest.factory.service.RestService;
import com.google.common.collect.Lists;
import com.openpojo.random.RandomFactory;
import com.openpojo.random.RandomGenerator;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

@RestService
public class TestService implements TestApi {
    @Override
    public void runnable() {
        System.out.println("RAN " + System.currentTimeMillis());
    }

    @Override
    public void consumer(final Pojo c) {
        System.out.println("CONSUMED [" + c + "] " + System.currentTimeMillis());
    }

    @Override
    public Pojo producer() {
        final Pojo product = RandomFactory.getRandomValue(Pojo.class);
        System.out.println("PRODUCED [" + product + "] " + System.currentTimeMillis());
        return product;
    }

    @Override
    public Pojo reverse(final Pojo c) {
        int[] copy = ArrayUtils.clone(c.getData());
        ArrayUtils.reverse(copy);
        final Pojo product = new Pojo(StringUtils.reverse(c.getValue()), copy);
        System.out.println("ADAPTED [" + c + "] to [" + product + "] " + System.currentTimeMillis());
        return product;
    }

    @Override
    public Pojo concatenate(final Pojo a, final Pojo b) {
        final Pojo product = new Pojo(a.getValue() + " " + b.getValue(), ArrayUtils.addAll(a.getData(), b.getData()));
        System.out.println("Concatenate [" + a + "] and [" + b + "] to [" + product + "] " + System.currentTimeMillis());
        return product;
    }

    @Override
    public int add(final int a, final int b) {
        final int sum = a + b;
        System.out.println("Add [" + a + "] and [" + b + "] to [" + sum + "] " + System.currentTimeMillis());
        return sum;
    }

    @Override
    public int sum(final int... values) {
        final int sum = IntStream.of(values).sum();
        System.out.println("Sum [" + Arrays.toString(values) + "] to [" + sum + "] " + System.currentTimeMillis());
        return sum;
    }

    @Override
    public List<String> validate(final String url) {
        final List<String> results = new ArrayList<>();
        RandomFactory.addRandomGenerator(new RandomGenerator() {
            @Override
            public Collection<Class<?>> getTypes() {
                return Lists.newArrayList(Pojo.class);
            }

            @Override
            public Object doGenerate(final Class<?> type) {
                return new Pojo(RandomFactory.getRandomValue(String.class), RandomFactory.getRandomValue(int[].class));
            }
        });
        final TestApi client = RestClientFactory.create(TestApi.class, ()->url);

        results.add(validateReverseFunction(client));
        results.add(validateConcatenateFunction(client));
        results.add(validateSumFunction(client));

        return results;
    }

    private String validateReverseFunction(final TestApi client) {
        final Pojo input = RandomFactory.getRandomValue(Pojo.class);
        int[] copy = ArrayUtils.clone(input.getData());
        ArrayUtils.reverse(copy);
        final Pojo expected = new Pojo(StringUtils.reverse(input.getValue()), copy);

        final Pojo actual = client.reverse(input);

        return "reverse(" + input + "), expected [" + expected + "], got [" + actual + "], is valid [" +
                ( expected.getValue().equals(actual.getValue()) && Arrays.equals(expected.getData(), actual.getData()))+ "]\n";
    }

    private String validateConcatenateFunction(final TestApi client) {
        final Pojo a = RandomFactory.getRandomValue(Pojo.class);
        final Pojo b = RandomFactory.getRandomValue(Pojo.class);

        final Pojo expected = new Pojo(a.getValue() + " " + b.getValue(), ArrayUtils.addAll(a.getData(), b.getData()));

        final Pojo actual = client.concatenate(a, b);

        return "concatenate(" + a + ", " + b + "), expected [" + expected + "], got [" + actual + "], is valid [" +
                ( expected.getValue().equals(actual.getValue()) && Arrays.equals(expected.getData(), actual.getData()))+ "]\n";
    }

    private String validateSumFunction(final TestApi client) {
        final int a = RandomFactory.getRandomValue(int.class);
        final int b = RandomFactory.getRandomValue(int.class);

        final int expected = a + b;

        final int actual = client.add(a, b);

        return "add(" + a + ", " + b + "), expected [" + expected + "], got [" + actual + "], is valid [" +
                ( expected == actual)+ "]\n";
    }

}
