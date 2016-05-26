package com.github.tcurrie.rest.factory.it.impls;

import com.github.tcurrie.rest.factory.it.apis.Pojo;
import com.github.tcurrie.rest.factory.it.apis.TestApi;
import com.github.tcurrie.rest.factory.service.RestService;
import com.github.tcurrie.rest.factory.v1.RestFactoryException;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestService
public class TestService implements TestApi {
    public static final Map<String, Object> DATA = new HashMap<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(TestService.class);

    @Override
    public void runnable() {
        LOGGER.debug("RAN " + System.currentTimeMillis());
        DATA.compute("runs", (k,v)->v==null?1:((Integer)v)+1);
    }

    @Override
    public void consumer(final Pojo c) {
        LOGGER.debug("CONSUMED [" + c + "] " + System.currentTimeMillis());
        DATA.put("consumed", c);
    }

    @Override
    public Pojo producer() {
        final Pojo product = (Pojo) DATA.get("produce");
        LOGGER.debug("PRODUCED [" + product + "] " + System.currentTimeMillis());
        return product;
    }

    @Override
    public Pojo reverse(final Pojo c) {
        int[] copy = ArrayUtils.clone(c.getData());
        ArrayUtils.reverse(copy);
        final Pojo product = new Pojo(StringUtils.reverse(c.getValue()), copy);
        LOGGER.debug("ADAPTED [" + c + "] to [" + product + "] " + System.currentTimeMillis());
        return product;
    }

    @Override
    public Pojo concatenate(final Pojo a, final Pojo b) {
        final Pojo product = new Pojo(a.getValue() + " " + b.getValue(), ArrayUtils.addAll(a.getData(), b.getData()));
        LOGGER.debug("Concatenate [" + a + "] and [" + b + "] to [" + product + "] " + System.currentTimeMillis());
        return product;
    }

    @Override
    public Set<Pojo> dedup(final Pojo... values) {
        return Sets.newHashSet(values);
    }

    @Override
    public Pojo min(final Set<Pojo> values) {
        final List<Pojo> sorted = Lists.newArrayList(values);
        Collections.sort(sorted, (a, b) -> a.getValue().compareTo(b.getValue()));
        return sorted.get(0);
    }

    @Override
    public int add(final int a, final int b) {
        final int sum = a + b;
        LOGGER.debug("Add [" + a + "] and [" + b + "] to [" + sum + "] " + System.currentTimeMillis());
        return sum;
    }

    @Override
    public int sum(final int... values) {
        final int sum = IntStream.of(values).sum();
        LOGGER.debug("Sum [" + Arrays.toString(values) + "] to [" + sum + "] " + System.currentTimeMillis());
        return sum;
    }

    @Override
    public int throwsException() throws Exception {
        throw (Exception) DATA.get("exception");
    }

    @Override
    public int throwsRuntimeException() {
        throw (RuntimeException) DATA.get("runtimeException");
    }

    @Override
    public String join(final String[] values) {
        final String result = Arrays.stream(values).collect(Collectors.joining());
        LOGGER.debug("Join [{}] to [{}] ", Arrays.toString(values), result);
        return result;
    }

    @Override
    public String join(final String[] values, final String separator) {
        if (separator == null) throw new RestFactoryException("No separator.");
        final String result = Arrays.stream(values).collect(Collectors.joining(separator));
        LOGGER.debug("Join [{}] with [{}] to [{}] ", Arrays.toString(values), separator, result);
        return result;
    }
}
