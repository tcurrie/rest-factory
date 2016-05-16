package com.github.tcurrie.rest.factory.it.impls;

import com.github.tcurrie.rest.factory.it.apis.Pojo;
import com.github.tcurrie.rest.factory.it.apis.TestApi;
import com.github.tcurrie.rest.factory.service.RestService;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.stream.IntStream;

@RestService
public class TestService implements TestApi {
    public static final Map<String, Object> DATA = new HashMap<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(TestService.class);

    @Override
    public void runnable() {
        LOGGER.info("RAN " + System.currentTimeMillis());
        DATA.compute("runs", (k,v)->v==null?1:((Integer)v)+1);
    }

    @Override
    public void consumer(final Pojo c) {
        LOGGER.info("CONSUMED [" + c + "] " + System.currentTimeMillis());
        DATA.put("consumed", c);
    }

    @Override
    public Pojo producer() {
        final Pojo product = (Pojo) DATA.get("produce");
        LOGGER.info("PRODUCED [" + product + "] " + System.currentTimeMillis());
        return product;
    }

    @Override
    public Pojo reverse(final Pojo c) {
        int[] copy = ArrayUtils.clone(c.getData());
        ArrayUtils.reverse(copy);
        final Pojo product = new Pojo(StringUtils.reverse(c.getValue()), copy);
        LOGGER.info("ADAPTED [" + c + "] to [" + product + "] " + System.currentTimeMillis());
        return product;
    }

    @Override
    public Pojo concatenate(final Pojo a, final Pojo b) {
        final Pojo product = new Pojo(a.getValue() + " " + b.getValue(), ArrayUtils.addAll(a.getData(), b.getData()));
        LOGGER.info("Concatenate [" + a + "] and [" + b + "] to [" + product + "] " + System.currentTimeMillis());
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
        LOGGER.info("Add [" + a + "] and [" + b + "] to [" + sum + "] " + System.currentTimeMillis());
        return sum;
    }

    @Override
    public int sum(final int... values) {
        final int sum = IntStream.of(values).sum();
        LOGGER.info("Sum [" + Arrays.toString(values) + "] to [" + sum + "] " + System.currentTimeMillis());
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
}
