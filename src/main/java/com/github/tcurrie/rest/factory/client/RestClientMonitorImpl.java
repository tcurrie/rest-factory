package com.github.tcurrie.rest.factory.client;

import com.github.tcurrie.rest.factory.proxy.ProxyMethodHandler;
import com.github.tcurrie.rest.factory.service.RestService;
import com.github.tcurrie.rest.factory.v1.RestClientMonitor;
import com.github.tcurrie.rest.factory.v1.RestMethodVerificationResult;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("WeakerAccess")
@RestService
public class RestClientMonitorImpl implements RestClientMonitor {
    private final static Set<ProxyMethodHandler<RestClientMethod<?>>> handlers =
            Collections.synchronizedSet(new HashSet<>());

    @SuppressWarnings("unchecked")
    static <T> void addHandler(final T handler) {
        handlers.add((ProxyMethodHandler<RestClientMethod<?>>)handler);
    }

    @Override
    public Set<RestMethodVerificationResult> verifyClients() {
        return handlers.stream().flatMap(h -> RestClientFactory.verify(h).stream()).collect(Collectors.toSet());
    }
}
