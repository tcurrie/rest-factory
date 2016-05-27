package com.github.tcurrie.rest.factory.it;

import com.github.tcurrie.rest.factory.client.RestClientFactory;
import com.github.tcurrie.rest.factory.it.apis.TestApi;
import com.github.tcurrie.rest.factory.v1.RestClientMonitor;
import com.github.tcurrie.rest.factory.v1.RestFactoryException;
import com.github.tcurrie.rest.factory.v1.RestMethodDictionary;
import com.github.tcurrie.rest.factory.v1.TimeOut;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

class TestClients {
    private TestClients() {
        throw new RestFactoryException("Can not construct instance of Factory class.");
    }

    static final Supplier<String> URL_SUPPLIER = () -> RestServers.SERVER.getUrl() + "/generated-rest";
    static final Supplier<String> INVALID_URL_SUPPLIER = () -> "invalid";
    private static final Supplier<TimeOut> TIME_OUT_SUPPLIER = () -> TimeOut.create(30, TimeUnit.SECONDS);
    private static final TestApi VALID_TEST_API = RestClientFactory.create(TestApi.class, URL_SUPPLIER, TIME_OUT_SUPPLIER);
    private static final TestApi INVALID_TEST_API = RestClientFactory.create(TestApi.class, INVALID_URL_SUPPLIER, TIME_OUT_SUPPLIER);
    private static final RestClientMonitor REST_CLIENT_MONITOR = RestClientFactory.create(RestClientMonitor.class, URL_SUPPLIER, TIME_OUT_SUPPLIER);
    private static final RestMethodDictionary REST_METHOD_DICTIONARY = RestClientFactory.create(RestMethodDictionary.class, URL_SUPPLIER, TIME_OUT_SUPPLIER);

    static TestApi getValidTestApi() {
        return VALID_TEST_API;
    }

    static TestApi getInvalidTestApi() {
        return INVALID_TEST_API;
    }

    static RestClientMonitor getRestClientMonitor() {
        return REST_CLIENT_MONITOR;
    }

    static RestMethodDictionary getRestMethodDictionary() {
        return REST_METHOD_DICTIONARY;
    }
}
