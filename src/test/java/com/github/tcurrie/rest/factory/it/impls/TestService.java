package com.github.tcurrie.rest.factory.it.impls;

import com.github.tcurrie.rest.factory.it.WebDriverTestBasis;
import com.github.tcurrie.rest.factory.it.apis.Pojo;
import com.github.tcurrie.rest.factory.it.apis.TestApi;
import com.github.tcurrie.rest.factory.service.RestService;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

@RestService
public class TestService implements TestApi {
    public static final Map<String, Object> DATA = new HashMap<>();
    private static final Logger LOGGER = Logger.getLogger(WebDriverTestBasis.class.getName());

    @Override
    public void runnable() {
        LOGGER.log(Level.INFO, "RAN " + System.currentTimeMillis());
        DATA.compute("runs", (k,v)->v==null?1:((Integer)v)+1);
    }

    @Override
    public void consumer(final Pojo c) {
        LOGGER.log(Level.INFO, "CONSUMED [" + c + "] " + System.currentTimeMillis());
        DATA.put("consumed", c);
    }

    @Override
    public Pojo producer() {
        final Pojo product = (Pojo) DATA.get("produce");
        LOGGER.log(Level.INFO, "PRODUCED [" + product + "] " + System.currentTimeMillis());
        return product;
    }

    @Override
    public Pojo reverse(final Pojo c) {
        int[] copy = ArrayUtils.clone(c.getData());
        ArrayUtils.reverse(copy);
        final Pojo product = new Pojo(StringUtils.reverse(c.getValue()), copy);
        LOGGER.log(Level.INFO, "ADAPTED [" + c + "] to [" + product + "] " + System.currentTimeMillis());
        return product;
    }

    @Override
    public Pojo concatenate(final Pojo a, final Pojo b) {
        final Pojo product = new Pojo(a.getValue() + " " + b.getValue(), ArrayUtils.addAll(a.getData(), b.getData()));
        LOGGER.log(Level.INFO, "Concatenate [" + a + "] and [" + b + "] to [" + product + "] " + System.currentTimeMillis());
        return product;
    }

    @Override
    public int add(final int a, final int b) {
        final int sum = a + b;
        LOGGER.log(Level.INFO, "Add [" + a + "] and [" + b + "] to [" + sum + "] " + System.currentTimeMillis());
        return sum;
    }

    @Override
    public int sum(final int... values) {
        final int sum = IntStream.of(values).sum();
        LOGGER.log(Level.INFO, "Sum [" + Arrays.toString(values) + "] to [" + sum + "] " + System.currentTimeMillis());
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
