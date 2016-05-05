package com.github.tcurrie.rest.factory.service;

import com.github.tcurrie.rest.factory.RestFactoryException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

final class UriSetRestHandlerDictionary implements RestMethodDictionary {
    // TODO Replace with node search for speed
    private static final Logger LOGGER = Logger.getLogger(UriSetRestHandlerDictionary.class.getName());
    private final Set<RestMethod> handlers;

    static UriSetRestHandlerDictionary create(final Set<RestMethod> handlers) {
        final UriSetRestHandlerDictionary dictionary = new UriSetRestHandlerDictionary(handlers);
        dictionary.handlers.addAll(RestMethodFactory.create(dictionary));

        return dictionary;
    }

    private UriSetRestHandlerDictionary(final Set<RestMethod> beans) {
        this.handlers = beans;
    }

    @Override
    public List<MethodDescription> getMethods() {
        return handlers.stream().map(MethodDescription::create).collect(Collectors.toList());
    }

    public RestMethod getHandler(final HttpServletRequest req) {
        final String uri = req.getRequestURI();
        final RestMethod<?,?> handler = find(uri);
        if (handler == null) {
            throw RestFactoryException.create(LOGGER, "Failed to match request [{0}] to any Handler from [{1}]", req.getRequestURI(), handlers);
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
