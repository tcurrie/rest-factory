package com.github.tcurrie.rest.factory.service;

import com.github.tcurrie.rest.factory.Strings;
import com.github.tcurrie.rest.factory.v1.RestFactoryException;
import com.github.tcurrie.rest.factory.v1.RestMethodDictionary;
import com.github.tcurrie.rest.factory.v1.RestMethodDictionary.MethodDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class UriSetRestHandlerDictionary implements RequestDelegate {
    // TODO Replace with node search for speed
    private static final Logger LOGGER = LoggerFactory.getLogger(UriSetRestHandlerDictionary.class);
    private static final Function<RestMethod, MethodDescription> METHOD_TO_DESCRIPTION = h -> MethodDescription.create(h.getUri(),
            h.getImplementation().getMethodName(), h.getImplementation().getBeanName());
    private final Set<RestMethod> handlers;

    static UriSetRestHandlerDictionary create(final Stream<RestMethod> handlers) {

        final UriSetRestHandlerDictionary d = new UriSetRestHandlerDictionary();
        d.handlers.addAll(handlers.collect(Collectors.toSet()));
        d.handlers.addAll(RestMethodFactory.create((RestMethodDictionary) () ->
                d.handlers.stream().map(METHOD_TO_DESCRIPTION).collect(Collectors.toSet()))
                .collect(Collectors.toSet()));
        return d;
    }

    private UriSetRestHandlerDictionary() {
        this.handlers = new HashSet<>();
    }

    @Override
    public RestMethod getHandler(final HttpServletRequest req) {
        final String uri = req.getRequestURI();
        final RestMethod<?,?> handler = find(uri);
        if (handler == null) {
            LOGGER.warn("Failed to match request [{}] to any Handler from [{}]", req.getRequestURI(), handlers);
            throw new RestFactoryException(Strings.format("Failed to match request [{}] to any Handler from [{}]", req.getRequestURI(), handlers));
        }
        return handler;
    }

    private RestMethod<?,?> find(final String uri) {
        for (final RestMethod h : handlers) {
            if (uri.contains(h.getUri())) {
                return h;
            }
        }
        return null;
    }
}
